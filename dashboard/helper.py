from django.shortcuts import render

from .models import Cluster


def cluster_state(state):
    on_states = ['STARTING', 'BOOTSTRAPPING', 'RUNNING', 'WAITING']
    if state in on_states:
        return 'On'

    return 'Off'


def get_cluster_db(cluster_id):
    try:
        return Cluster.objects.get(cluster_id=cluster_id)
    except Cluster.DoesNotExist:
        return None


def render_cluster_doesnt_exist(request, cluster_id):
    return render(
        request,
        'dashboard/error_cluster_does_not_exist.html',
        {'cluster_id': cluster_id})


def get_value_from_request(request, key, default):
    if default is not None:
        return request.POST.get(key, default)
    else:
        return request.POST.get(key)


def append_to_context(response):
    data = []
    for i in response['Clusters']:
        cluster_id = i['Id']
        name = i['Name']
        state = cluster_state(i['Status']['State'])
        data.append({'id': cluster_id, 'name': name, 'state': state})

    return data


def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context
