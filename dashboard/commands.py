#
# EMR steps utilized to perform all operations
#

import subprocess
import json

from django.conf import settings


def get_step_command(cluster_id, step):
    return ['aws', 'emr', 'add-steps',
            '--cluster-id', cluster_id,
            '--steps', ''.join(step)]


def run_step(step_name, args, cluster_id, artifact_path):
    step = ['Name', '=', step_name, ',',
            'Jar', '=', artifact_path, ',',
            'ActionOnFailure', '=', 'CANCEL_AND_WAIT', ',',
            'Type', '=', 'CUSTOM_JAR', ',',
            'Args', '=', args]

    return subprocess.check_output(get_step_command(cluster_id, step))


def get_artifact_path_from_s3(artifact_name):
    return 's3://' + settings.TFG_BUCKET_NAME + '/artifacts/' + artifact_name


def step_load_data(cluster_id, path):
    step_name = 'load_data'

    args = 's3-dist-cp,--src,s3://' \
           + settings.TFG_BUCKET_NAME \
           + path \
           + ',--dest,/input'  # /input

    artifact_name = 'command-runner.jar'

    return run_step(step_name, args, cluster_id, artifact_name)


def step_unique_items(cluster_id, shards_number):
    step_name = 'unique_items'
    args = '/input/dataset,/output,' + shards_number
    artifact_name = 'tfg-hadoop-generate-unique-items.jar'
    artifact_path = get_artifact_path_from_s3(artifact_name)

    return run_step(step_name, args, cluster_id, artifact_path)


def step_recommend(cluster_id, no_of_shards):
    step_name = 'recommend'

    dataset_path = '/input/dataset'
    results_dir = '/output'
    active_users_path = '/input/active-users/users.csv'
    unique_items_path = '/output/part-r-00000'
    no_of_similarity_files = 'a'
    evaluation_type = 'b'
    evaluation_n_value = 'c'
    seed_random_generator = '0'
    args = dataset_path \
        + ',' + results_dir \
        + ',' + active_users_path \
        + ',' + unique_items_path \
        + ',' + no_of_shards \
        + ',' + no_of_similarity_files \
        + ',' + evaluation_type \
        + ',' + evaluation_n_value \
        + ',' + seed_random_generator

    artifact_name = 'tfg-hadoop-recommend-with-eval.jar'
    artifact_path = get_artifact_path_from_s3(artifact_name)

    return run_step(step_name, args, cluster_id, artifact_path)


#
# Commands to manipulate EMR clusters
#
def command_describe_cluster(cluster_id):
    command = ['aws', 'emr', 'describe-cluster',
               '--cluster-id', cluster_id]
    return subprocess.check_output(command)


def command_launch_cluster(name, instance_count):
    instance_type = 'm1.medium'
    instance_profile = 'InstanceProfile=EMR_EC2_DefaultRole,KeyName=' \
                       + settings.TFG_SSH_KEY

    command = ['aws', 'emr', 'create-cluster',
               '--name', name,
               '--log-uri', 's3://' + settings.TFG_BUCKET_NAME + '/logs',
               '--service-role', 'EMR_DefaultRole',
               '--ec2-attributes', instance_profile,
               '--instance-type', instance_type,
               '--release-label', 'emr-5.24.1',
               '--instance-count', instance_count,]

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


def command_active_users(dataset_path, active_users, n_active_users, seed):
    base_dir = "/home/diego/1.workspace"
    script = 'fic-tfg-scripts/generate-active-users.sh'

    command = [base_dir+'/'+script,
               dataset_path,
               active_users,
               n_active_users,
               seed]
    return subprocess.check_output(command)
