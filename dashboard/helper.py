from datetime import datetime
from django.shortcuts import render
from .models import Cluster


def get_cluster_state(state):
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
        cluster_name = item['Name']
        cluster_state = get_cluster_state(item['Status']['State'])

        cluster_ready_date_time = ''
        if 'ReadyDateTime' in item['Status']['Timeline']:
            cluster_ready_date_time = get_date_from_milis(
                item['Status']['Timeline']['ReadyDateTime'])

        cluster_end_date_time = ''
        if 'EndDateTime' in item['Status']['Timeline']:
            cluster_end_date_time = get_date_from_milis(
                item['Status']['Timeline']['EndDateTime'])

        data.append({
            'cluster_id': cluster_id,
            'cluster_name': cluster_name,
            'cluster_state': cluster_state,
            'cluster_ready_date_time': cluster_ready_date_time,
            'cluster_end_date_time': cluster_end_date_time})

    return data


def get_context_data():
    context = {
        'name_app': 'cluster'
    }
    return context


def get_date_from_milis(miliseconds):
    string_format = '%Y-%m-%d %H:%M:%S'
    value = int(miliseconds)
    return datetime.fromtimestamp(value).strftime(string_format)