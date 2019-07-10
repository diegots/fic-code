projects_base_dir = "/home/diego/1.workspace"
data_dir = 'fic-tfg-data-rbd7yz'
scripts_dir = 'fic-tfg-scripts'

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
        + '/' + data_dir \
        + '/' + dataset_names.get(size) \
        + '/' + dataset_dir + '/' + dataset_file
