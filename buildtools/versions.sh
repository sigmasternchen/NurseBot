#!/bin/bash

DEPLOY_DIR=$1
VERSIONS=./versions

ls $DEPLOY_DIR/$VERSIONS/*.jar | while read file; do
	echo $file | awk -F"NurseBot" '{ print $2 }' | awk -F".jar" '{ print $1 }'
done
