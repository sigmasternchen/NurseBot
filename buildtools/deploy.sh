#!/bin/bash

DEPLOY_DIR=$1
VERSIONS=./versions

VERSION=$2

pushd $DEPLOY_DIR

unlink NurseBot.jar
ln $VERSIONS/NurseBot$VERSION.jar NurseBot.jar

popd
