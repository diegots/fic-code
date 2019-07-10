from urllib.parse import urlencode

from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect
from django.urls import reverse
from django.utils import timezone
from django.http import HttpResponse

from .commands import *
from .paths import *
from .helper import *
from .models import Cluster


# ##### #
# Views #
# ##### #
@login_required
def index(request):
    return render(request, 'dashboard/dashboard.html')


#
# Cluster section
#
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

    res = command_launch_cluster(cluster_name, cluster_instances)
    cluster_id = json.loads(res.decode())['ClusterId']

    # Save data into database
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
        return render(request,
                      'dashboard/error_cluster_does_not_exist.html',
                      {'cluster_id': cluster_id})
    else:
        if query_set[0].off_date is None:
            query_set[0].off_date = timezone.now()
            query_set[0].save()
            command_terminate_cluster(cluster_id)
            return redirect(reverse('dashboard:cluster-terminate-result'))
        else:
            return render(request,
                          'dashboard/error_cluster_not_running.html',
                          {'cluster_id': cluster_id})


@login_required
def cluster_terminate_result(request):
    return render(request, 'dashboard/cluster_terminate_result.html')


#
# Recommendation section
#
@login_required
def recommend(request):
    return render(request, 'dashboard/recommend.html')


@login_required
def recommend_load_action(request):
    # First get cluster data
    cluster_id = request.POST.get('load-cluster-id')
    current_cluster = get_cluster_db(cluster_id)

    if current_cluster is None:
        return render_cluster_doesnt_exist(request, cluster_id)

    # Then load data into cluster
    step_load_data_result = step_load_data(
        cluster_id,
        get_dataset_path_s3(request.POST.get('load-dataset-size')))

    step_id_load_data = ''.join(
        json.loads(step_load_data_result.decode())['StepIds'])

    # At last prepare redirect url
    base_url = reverse('dashboard:recommend-load-result')
    query_string = urlencode({'step_id_load_data': step_id_load_data})
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)


@login_required
def recommend_load_result(request):
    context = get_context_data()
    context['step_id'] = request.GET.get('step_id_load_data')
    return render(request, 'dashboard/recommend_step_result.html', context)


@login_required
def recommend_unique_items_action(request):
    cluster_id = get_value_from_request(request, 'unique-items-cluster-id')
    current_cluster = get_cluster_db(cluster_id)

    if current_cluster is None:
        return render_cluster_doesnt_exist(request, cluster_id)

    # Now compute unique items
    step_unique_items_result = \
        step_unique_items(cluster_id, str(current_cluster.number_nodes))

    step_id_unique_items = ''.join(json.loads(
        step_unique_items_result.decode())['StepIds'])

    # At last prepare redirect url
    base_url = reverse('dashboard:recommend-unique-items-result')
    query_string = urlencode({'step_id_unique_items': step_id_unique_items})
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)


@login_required
def recommend_unique_items_result(request):
    context = get_context_data()
    context['step_id'] = request.GET.get('step_id_unique_items')
    return render(request, 'dashboard/recommend_step_result.html', context)


@login_required
def recommend_generate_active_users_action(request):
    dataset_size = request.POST.get('active-users-dataset-size')
    output_file = request.POST.get('active-users-output-file')
    n_active_users = request.POST.get('active-users-n-active-users')
    seed = request.POST.get('active-users-seed')

    print('dataset_size: ' + dataset_size
          + ', output_file: ' + output_file
          + ', n_active_users: ' + n_active_users
          + ', seed: ' + seed)

    # run command
    command_active_users_result = command_generate_active_users(dataset_size,
                                                                output_file,
                                                                n_active_users,
                                                                seed)

    # print('command_active_users_result: \n'
    #       + command_active_users_result.decode("utf-8"))

    command_move_file_bucket(output_file,
                             get_data_dir() + '/' + active_users_dir)
    command_sync_bucket()

    return render(request, 'dashboard/recommend_active_users_result.html')


@login_required
def recommend_generate_shards_action(request):
    # read cluster id
    cluster_id = request.POST.get('load-cluster-id')

    # obtain dns name
    res = command_describe_cluster(cluster_id)
    dns_name = json.loads(res)['Cluster']['MasterPublicDnsName']

    # copy jar
    local_command = 'aws s3 cp s3://' \
                    + settings.TFG_BUCKET_NAME \
                    + '/artifacts/generate.jar .'
    command_run_local(dns_name, local_command)

    # copy dataset
    local_command = 'aws s3 cp s3://' \
                    + settings.TFG_BUCKET_NAME \
                    + get_dataset_path_s3(
        request.POST.get('load-dataset-size')) \
                    + '/dataset/ratings.csv .'
    command_run_local(dns_name, local_command)

    # run shard generation
    local_command = 'java -jar generate.jar -matrix ratings.csv' \
                    + ' ' + request.POST.get('load-number-shards') \
                    + ' ' + request.POST.get('load-shard_prefix')
    command_run_local(dns_name, local_command)

    # create dest dir and move splits there
    local_command = 'rm -rf shards && mkdir shards && mv ' \
                    + request.POST.get('load-shard_prefix') + '*' \
                    + ' shards/'
    command_run_local(dns_name, local_command)

    # put shards back into S3
    local_command = 'aws s3 mv shards s3://' \
                    + settings.TFG_BUCKET_NAME \
                    + get_dataset_path_s3(request.POST.get('load-dataset-size')) \
                    + '/shards --recursive'
    command_run_local(dns_name, local_command)

    # load result page
    return HttpResponse('recommend_generate_shards_action')


@login_required
def recommend_generate_shards_result(request):
    return None


# TODO recommend_similarities


@login_required
def recommend_compute_action(request):
    # get data from request
    cluster_id = request.POST.get('recommend-cluster-id')
    no_of_shards = request.POST.get('recommend-no-of-shards')

    # TODO missing parameters for recommend action
    step_recommend_result = step_recommend(cluster_id, no_of_shards)

    step_id_recommend = ''.join(json.loads(
        step_recommend_result.decode())['StepIds'])

    # At last prepare redirect url
    base_url = reverse('dashboard:recommend-compute-action-result')
    query_string = urlencode({'step_id_compute': step_id_recommend})
    url = '{}?{}'.format(base_url, query_string)
    return redirect(url)


@login_required
def recommend_compute_action_result(request):
    context = get_context_data()
    context['step_id'] = request.GET.get('step_id_compute')
    return render(request, 'dashboard/recommend_step_result.html', context)


#
# Results section
#
@login_required
def result(request):
    return render(request, 'dashboard/result.html')
