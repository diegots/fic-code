from django.shortcuts import render

from .models import Cluster


def cluster_state(state):
    on_states = ['RUNNING', 'WAITING']
    booting_states = ['STARTING', 'BOOTSTRAPPING']

    if state in on_states:
        return 'On'
    if state in booting_states:
        return 'Booting up'

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


def get_value_from_request(request, key):
    return request.POST.get(key)


def append_to_context(response):
    data = []
    for i in response['Clusters']:
        cluster_id = i['Id']
        name = i['Name']
        state = cluster_state(i['Status']['State'])

        ready_date_time = ''
        if 'ReadyDateTime' in i['Status']['Timeline']:
            ready_date_time = i['Status']['Timeline']['ReadyDateTime']

        # TODO missing number of nodes
        # TODO missing DNS name
        data.append({
            'id': cluster_id,
            'name': name,
            'state': state,
            'ready_date_time': ready_date_time})

    return data


def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context
