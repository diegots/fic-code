#!/usr/bin/env bash
#
# Set variables
#

# Root dir for the application, e.g:
# DJANGO_ROOT_DIR="$HOME/path_to_repo"
DJANGO_ROOT_DIR=""

# Virtualenv directory name
# This is the name given to the virtualenv folder.
VIRTUALENV_DIR_NAME=''

# With the global variables set up, this script is meant to be manually run with
# 'source'  command like:
# user@host:~$ source $HOME/path_to_repo/scripts/active-python-env.sh
# Here finish the configurable section.

#
# Start service
#

# Make root dir path available to third commands
export DJANGO_ROOT_DIR

# Start python virtualenv
source $DJANGO_ROOT_DIR/$VIRTUALENV_DIR_NAME/bin/activate

# Activate Django global parameters
source $DJANGO_ROOT_DIR/scripts/global-vars.sh

# To start django development web server run:
# python3 $DJANGO_ROOT_DIR/manage.py runserver

