from django.conf import settings

projects_base_dir = "/home/diego/1.workspace"
scripts_dir = 'fic-tfg-scripts'
active_users_dir = 'active-users'

dataset_names = {
    '100K': 'input-100k',
    '1M': 'input-1m',
    '10M': 'input-10m',
    '20M': 'input-20m',
}


# Actual base paths on S3 where input data is kept. One entry for every
# dataset size
def get_dataset_path_s3(size):
    return '/' + dataset_names.get(size)


# Project local install paths from Django
def get_dataset_path_local(size):

    dataset_dir = 'dataset'
    dataset_file = 'ratings.csv'

    return projects_base_dir \
        + '/' + settings.TFG_BUCKET_NAME \
        + '/' + dataset_names.get(size) \
        + '/' + dataset_dir + '/' + dataset_file


def get_script_active_users():
    return projects_base_dir + '/' + scripts_dir \
           + '/' + 'generate-active-users.sh'


def get_artifact_from_s3(artifact_name):
    return 's3://' + settings.TFG_BUCKET_NAME + '/artifacts/' + artifact_name


def get_data_dir():
    return projects_base_dir + '/' + settings.TFG_BUCKET_NAME


def get_bucket_dir():
    return 's3://' + settings.TFG_BUCKET_NAME + '/'
