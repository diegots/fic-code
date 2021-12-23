# S3 bucket name where data will be stored to and read from.
export TFG_BUCKET_NAME=''

# Django secret key. This shoud be a long random string. Some password generator may be used.
export TFG_SECRET_KEY=''

# SSH key used to allow access to the cluster machines. This is just the key's name. Have to be registered with ssh-agent or similar.
export TFG_SSH_KEY=''

# Hosts allowed to use the service.
export TFG_ALLOWED_HOSTS='127.0.0.1'

# Activate Django's debugging capabilities.
export TFG_DEBUG='0'

