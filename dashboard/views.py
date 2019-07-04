import json
import subprocess
from urllib.parse import urlencode

from django.conf import settings
from django.contrib.auth.decorators import login_required
from django.shortcuts import render, redirect
from django.urls import reverse
from django.utils import timezone
from django.http import HttpResponse

from .models import Cluster

#
# Actual base paths on S3 where input data is kept. One entry for every
# dataset size
#
input_data_path = {
    '100k': '/input-100k',
    '1M': '/input-1m'
}


#
# EMR steps utilized to perform all operations
#
def command_base(cluster_id, step):
    return ['aws', 'emr', 'add-steps',
            '--cluster-id', cluster_id,
            '--steps', ''.join(step)]


def step_load_data(cluster_id, path):
    args = 's3-dist-cp,--src,s3://' + settings.TFG_BUCKET_NAME + path + ',--dest,/input'  # /input
    step = ['Name', '=', 'load_data', ',',
            'Jar', '=', 'command-runner.jar', ',' ,
            'ActionOnFailure', '=', 'CANCEL_AND_WAIT', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args]
    return subprocess.check_output(command_base(cluster_id, step))


def step_unique_items(cluster_id, shards_number):
    args = '/input/dataset,/output,' + shards_number
    step = ['Name', '=', 'unique-items', ',',
            'Jar', '=', 's3://' + settings.TFG_BUCKET_NAME + '/artifacts/tfg-hadoop-generate-unique-items.jar', ',',
            'ActionOnFailure', '=',  'CANCEL_AND_WAIT', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args]
    return subprocess.check_output(command_base(cluster_id, step))


def step_recommendations(cluster_id, no_of_shards):

    dataset_path = '/input/dataset'
    results_dir = '/output'
    active_users_path = '/input/active-users/users.csv'
    unique_items_path = '/output/part-r-00000'
    no_of_similarity_files = ''
    evaluation_type = ''
    evaluation_n_value = ''
    seed_random_generator = ''
    args = dataset_path \
        + ',' + results_dir \
        + ',' + active_users_path \
        + ',' + unique_items_path \
        + ',' + no_of_shards \
        + ',' + no_of_similarity_files \
        + ',' + evaluation_type \
        + ',' + evaluation_n_value \
        + ',' + seed_random_generator

    artifact_path = 's3://' + settings.TFG_BUCKET_NAME \
                    + '/artifacts/tfg-hadoop-recommend-with-eval.jar'

    step = ['Name', '=', 'recommendations', ',',
            'Jar', '=', artifact_path, ',',
            'ActionOnFailure', '=',  'CONTINUE', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args, ]

    return subprocess.check_output(command_base(cluster_id, step))


#
# Commands to manipulate EMR clusters
#
def command_describe_cluster(cluster_id):
    command = ['aws', 'emr', 'describe-cluster',
               '--cluster-id', cluster_id]
    return subprocess.check_output(command)


def command_launch_cluster(name, instance_count):
    instance_type = 'm1.medium'
    command = ['aws', 'emr', 'create-cluster',
               '--name', name,
               '--log-uri', 's3://' + settings.TFG_BUCKET_NAME + '/logs',
               '--service-role', 'EMR_DefaultRole',
               '--ec2-attributes', 'InstanceProfile=EMR_EC2_DefaultRole,KeyName=' + settings.TFG_SSH_KEY,
               '--instance-type', instance_type,
               '--release-label', 'emr-5.21.0',
               '--instance-count', instance_count]
    return subprocess.check_output(command)


def command_list_cluster(active):
    if active is True:
        command = ['aws', 'emr', 'list-clusters', '--active']
    else:
        command = ['aws', 'emr', 'list-clusters']
    return json.loads(subprocess.check_output(command))


def command_terminate_cluster(cluster_id):
    command = ['aws', 'emr', 'terminate-clusters',
               '--cluster-ids', cluster_id]
    return subprocess.check_output(command)


# Compute locally on the master node
def command_run_local(host, remote_command):
    command = ['ssh', '-oStrictHostKeyChecking=no',
               '-i', settings.TFG_SSH_KEY,
               'ec2-user@' + host, remote_command]
    return subprocess.check_output(command)


#
# Helper functions
#
def cluster_state(state):
    on_states = ['STARTING', 'BOOTSTRAPPING', 'RUNNING', 'WAITING']
    if state in on_states:
        return 'On'

    return 'Off'


def append_to_context(response):
    data = []
    for cluster in response['Clusters']:
        cluster_id = cluster['Id']
        name = cluster['Name']
        state = cluster_state(cluster['Status']['State'])
        data.append({'id': cluster_id, 'name': name, 'state': state})
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
    cluster_list_response = command_list_cluster(True)
    context = get_context_data()
    context['data'] = append_to_context(cluster_list_response)
    return render(request, 'dashboard/cluster_list.html', context)


@login_required
def cluster_terminate(request):
    cluster_list_response = command_list_cluster(True)
    context = get_context_data()
    context['data'] = append_to_context(cluster_list_response)
    return render(request, 'dashboard/cluster_terminate.html', context)


@login_required
def cluster_terminate_action(request):
    cluster_id = request.POST.get('cluster_id', '')

    query_set = Cluster.objects.filter(cluster_id=cluster_id)
    if len(query_set) < 1:
        return render(request, 'dashboard/error_cluster_does_not_exist.html', {'cluster_id': cluster_id})
    else:
        if query_set[0].off_date is None:
            query_set[0].off_date = timezone.now()
            query_set[0].save()
            command_terminate_cluster(cluster_id)
            return redirect(reverse('dashboard:cluster-terminate-result'))
        else:
            return render(request, 'dashboard/error_cluster_not_running.html', {'cluster_id': cluster_id})


@login_required
def cluster_terminate_result(request):
    return render(request, 'dashboard/cluster_terminate_result.html')


@login_required
def recommend(request):
    return render(request, 'dashboard/recommend.html')


@login_required
def recommend_compute_action(request):

    return HttpResponse('recommend_compute_action')


@login_required
def recommend_load_action(request):

    # First get cluster data
    cluster_id = request.POST.get('load-cluster-id')
    try:
        cluster = Cluster.objects.get(cluster_id=cluster_id)
    except Cluster.DoesNotExist:
        return render(request, 'dashboard/error_cluster_does_not_exist.html', {'cluster_id': cluster_id})

    # Then load data into cluster
    result = step_load_data(cluster_id, input_data_path[request.POST.get('load-dataset-size')])
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
def recommend_generate_active_users_action(request):
    return None


@login_required
def recommend_generate_shards_action(request):
    # read cluster id
    cluster_id = request.POST.get('load-cluster-id')

    # obtain dns name
    result = command_describe_cluster(cluster_id)
    dns_name = json.loads(result)['Cluster']['MasterPublicDnsName']

    # copy jar
    local_command = 'aws s3 cp s3://' + settings.TFG_BUCKET_NAME + '/artifacts/generate.jar .'
    command_run_local(dns_name, local_command)

    # copy dataset
    local_command = 'aws s3 cp s3://' \
                    + settings.TFG_BUCKET_NAME \
                    + input_data_path[request.POST.get('load-dataset-size')] \
                    + '/dataset/ratings.csv .'
    command_run_local(dns_name, local_command)

    # run shard generation
    local_command = 'java -jar generate.jar -matrix ratings.csv' \
                    + ' ' + request.POST.get('load-number-shards') \
                    + ' ' + request.POST.get('load-shard_prefix')
    command_run_local(dns_name, local_command)

    # create dest dir and move splits there
    local_command = 'rm -rf shards && mkdir shards && mv ' + request.POST.get('load-shard_prefix') + '*' + ' shards/'
    command_run_local(dns_name, local_command)

    # put shards back into S3
    local_command = 'aws s3 mv shards s3://' \
                    + settings.TFG_BUCKET_NAME \
                    + input_data_path[request.POST.get('load-dataset-size')] \
                    + '/shards --recursive'
    command_run_local(dns_name, local_command)

    # load result page
    return HttpResponse('response')


@login_required
def recommend_generate_shards_result(request):
    return None


@login_required
def result(request):
    return render(request, 'dashboard/result.html')
