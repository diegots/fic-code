import subprocess
import json

from django.shortcuts import render, redirect
from django.urls import reverse
from urllib.parse import urlencode

def append_to_context(response):
    data = []
    for cluster in response['Clusters']:
        id = cluster['Id']
        name = cluster['Name']
        state = cluster_state(cluster['Status']['State'])
        data.append({'id': id, 'name': name, 'state': state})
    return data


def cluster_state(state):
    on_states = ['STARTING', 'BOOTSTRAPPING', 'RUNNING', 'WAITING']
    if state in on_states:
        return 'On'

    return 'Off'


def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context


def get_clusters(active):
    if active is True:
        command = ['aws', 'emr', 'list-clusters', '--active']
    else:
        command = ['aws', 'emr', 'list-clusters']
    return json.loads(subprocess.check_output(command))


def launch_cluster(name, instance_count):
    command = ['aws', 'emr', 'create-cluster',
               '--name', name,
               '--service-role', 'EMR_DefaultRole',
               '--ec2-attributes', 'InstanceProfile=EMR_EC2_DefaultRole',
               '--instance-type', 'm1.medium',
               '--release-label', 'emr-5.21.0',
               '--instance-count', instance_count
               ]
    return subprocess.check_output(command)


def terminate_cluster(id):
    command = ['aws', 'emr', 'terminate-clusters',
               '--cluster-ids', id]
    return subprocess.check_output(command)

# Views
def list(request):
    response = get_clusters(False)
    context = get_context_data()
    context['name_section'] = 'List'
    context['data'] = append_to_context(response)
    return render(request, 'cluster/list.html', context)


def launch(request):
    context = get_context_data()
    context['name_section'] = 'Launch'
    return render(request,'cluster/launch.html', context)


def launch_action(request):
    cluster_name = request.POST.get('cluster_name', 'unnamed')
    cluster_intances_number = request.POST.get('cluster_intances_number', '0')
    result = launch_cluster(cluster_name, cluster_intances_number)
    cluster_id = json.loads(result.decode())['ClusterId']
    base_url = reverse('cluster:launch_result')
    query_string = urlencode({'cluster_name': cluster_name,
                              'cluster_intances_number': cluster_intances_number,
                              'cluster_id': cluster_id},)
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)


def launch_result(request):
    context = get_context_data()
    context['name_section'] = 'Result'
    context['cluster_name'] = request.GET.get('cluster_name')
    context['cluster_intances_number'] = int(request.GET.get('cluster_intances_number'))
    context['cluster_id'] = request.GET.get('cluster_id')
    return render(request, 'cluster/launch_result.html', context)


def terminate(request):
    response = get_clusters(True)
    context = get_context_data()
    context['name_section'] = 'Terminate'
    context['data'] = append_to_context(response)
    return render(request, 'cluster/terminate.html', context)


def terminate_action(request):
    cluster_id =  request.POST.get('cluster_id', 'unnamed')
    terminate_cluster(cluster_id)
    return redirect(reverse('cluster:terminate_result'))


def terminate_result(request):
    context = get_context_data()
    context['name_section'] = 'Result'
    return render(request, 'cluster/terminate_result.html', context)
