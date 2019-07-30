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
    for item in response['Clusters']:
        cluster_id = item['Id']
        name = item['Name']
        state = cluster_state(item['Status']['State'])

        ready_date_time = ''
        if 'ReadyDateTime' in item['Status']['Timeline']:
            ready_date_time = item['Status']['Timeline']['ReadyDateTime']

        end_date_time = ''
        if 'EndDateTime' in item['Status']['Timeline']:
            end_date_time = item['Status']['Timeline']['EndDateTime']

        # TODO missing number of nodes
        # TODO missing DNS name
        data.append({
            'id': cluster_id,
            'name': name,
            'state': state,
            'ready_date_time': ready_date_time,
            'end_date_time': end_date_time})

    return data


def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context
