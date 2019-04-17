import json
import subprocess
from urllib.parse import urlencode

from django.conf import settings
from django.contrib.auth.decorators import login_required
from django.http import HttpResponse, Http404
from django.shortcuts import render, redirect
from django.urls import reverse

from .models import Cluster


#
# Amazon EMR commands
#
dataset_path = {
    '100k': '/input-100k',
    '1M': '/input-1m'
}

def command_base(cluster_id, step):
    return ['aws', 'emr', 'add-steps',
           '--cluster-id', cluster_id,
           '--steps', ''.join(step)]

def step_load_data(cluster_id, path):
    args = 's3-dist-cp,--src,s3://' + settings.TFG_BUCKET_NAME + path + ',--dest,' + path # /input
    step = ['Name', '=', 'load_data', ',',
            'Jar', '=', 'command-runner.jar', ',' ,
            'ActionOnFailure', '=', 'CANCEL_AND_WAIT', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args]
    return subprocess.check_output(command_base(cluster_id, step))

def step_unique_items(cluster_id, shards_number):
    args = '/input/dataset,/output,' + shards_number
    step = ['Name', '=', 'unique-items', ',',
            'Jar', '=', 's3://' + settings.TFG_BUCKET_NAME + '/artifacts/hadoop-unique-items.jar', ',',
            'ActionOnFailure', '=',  'CANCEL_AND_WAIT', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args]
    return subprocess.check_output(command_base(cluster_id, step))

def step_recommendations(cluster_id, shards_number):
    args = '/input/dataset,/output,/input/active-users/users.csv,/output/part-r-00000,' + shards_number
    step = ['Name', '=', 'recomendations', ',',
            'Jar', '=', 's3://' + settings.TFG_BUCKET_NAME + '/artifacts/hadoop-recomendations.jar', ',',
            'ActionOnFailure', '=',  'CONTINUE', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args,]
    return subprocess.check_output(command_base(cluster_id, step))

def command_launch_cluster(name, instance_count):
    command = ['aws', 'emr', 'create-cluster',
               '--name', name,
               '--log-uri', 's3://u8ns72b/logs',
               '--service-role', 'EMR_DefaultRole',
               '--ec2-attributes', 'InstanceProfile=EMR_EC2_DefaultRole,KeyName=' + settings.SSH_KEY_NAME,
               '--instance-type', 'm1.medium',
               '--release-label', 'emr-5.21.0',
               '--instance-count', instance_count]
    return subprocess.check_output(command)

def command_terminate_cluster(id):
    command = ['aws', 'emr', 'terminate-clusters',
               '--cluster-ids', id]
    return subprocess.check_output(command)


#
# Get historic data on clusters
#
def get_historic_clusters(active):
    if active is True:
        command = ['aws', 'emr', 'list-clusters', '--active']
    else:
        command = ['aws', 'emr', 'list-clusters']
    return json.loads(subprocess.check_output(command))

def cluster_state(state):
    on_states = ['STARTING', 'BOOTSTRAPPING', 'RUNNING', 'WAITING']
    if state in on_states:
        return 'On'

    return 'Off'

def append_to_context(response):
    data = []
    for cluster in response['Clusters']:
        id = cluster['Id']
        name = cluster['Name']
        state = cluster_state(cluster['Status']['State'])
        data.append({'id': id, 'name': name, 'state': state})
    return data

def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context


#
# Views
#
@login_required
def index(request):
    return render(request, 'dashboard/dashboard.html')

@login_required
def cluster(request):
    return render(request, 'dashboard/cluster.html')

@login_required
def cluster_launch(request):
    return render(request, 'dashboard/cluster_launch.html')

@login_required
def cluster_launch_action(request):
    cluster_name = request.POST.get('cluster_name', 'unnamed')
    cluster_instances = request.POST.get('cluster_instances', '0')

    result = command_launch_cluster(cluster_name, cluster_instances)
    cluster_id = json.loads(result.decode())['ClusterId']

    # Save data into BBDD
    c = Cluster(cluster_id=cluster_id, number_nodes=cluster_instances)
    c.save()

    # Create url
    base_url = reverse('dashboard:cluster-launch-result')
    query_string = urlencode({'cluster_name': cluster_name,
                              'cluster_instances': cluster_instances,
                              'cluster_id': cluster_id})
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)

@login_required
def cluster_launch_result(request):
    context = get_context_data()
    context['cluster_name'] = request.GET.get('cluster_name')
    context['cluster_instances'] = int(request.GET.get('cluster_instances'))
    context['cluster_id'] = request.GET.get('cluster_id')
    return render(request, 'dashboard/cluster_launch_result.html', context)

@login_required
def cluster_list(request):
    cluster_list_response = get_historic_clusters(True)
    context = get_context_data()
    context['data'] = append_to_context(cluster_list_response)
    return render(request, 'dashboard/cluster_list.html', context)

@login_required
def cluster_terminate(request):
    cluster_list_response = get_historic_clusters(True)
    context = get_context_data()
    context['data'] = append_to_context(cluster_list_response)
    return render(request, 'dashboard/cluster_terminate.html', context)

@login_required
def cluster_terminate_action(request):
    cluster_id = request.POST.get('cluster_id', '')
    command_terminate_cluster(cluster_id)
    return redirect(reverse('dashboard:cluster-terminate-result'))

@login_required
def cluster_terminate_result(request):
    return render(request, 'dashboard/cluster_terminate_result.html')

@login_required
def recommend(request):
    return render(request, 'dashboard/recommend.html')

@login_required
def recommend_load_action(request):

    # First get cluster data
    cluster_id = request.POST.get('load-cluster-id')
    try:
        cluster = Cluster.objects.get(cluster_id=cluster_id)
    except Cluster.DoesNotExist:
        return render(request, 'dashboard/recommend_load_cluster_does_not_exist.html', {'cluster_id': cluster_id})

    # Then load data into cluster
    result = step_load_data(cluster_id, dataset_path[request.POST.get('load-dataset-size')])
    step_id_load_data = ''.join(json.loads(result.decode())['StepIds'])

    # Now compute unique items
    result = step_unique_items(cluster_id, str(cluster.number_nodes))
    step_id_unique_items = ''.join(json.loads(result.decode())['StepIds'])

    # At last prepare redirect url
    base_url = reverse('dashboard:recommend-load-result')
    query_string = urlencode({'step_id_load_data': step_id_load_data,
                              'step_id_unique_items': step_id_unique_items})
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)

@login_required
def recommend_load_result(request):
    context = get_context_data()
    context['step_id'] = request.GET.get('step_id')
    return render(request, 'dashboard/recommend_load_result.html', context)

@login_required
def recomm_prepare_result(request):
    return HttpResponse('recommend prepare result')

@login_required
def result(request):
    return render(request, 'dashboard/result.html')

@login_required
def recomm_generate_shards(request):
    return HttpResponse('recomm_generate_shards')

@login_required
def recomm_generate_users(request):
    return HttpResponse('recomm_generate_users')

@login_required
def recomm_compute(request):
    return HttpResponse('recomm_recomm')

