#!/bin/bash

git checkout production
if test ! $? = 0; then
	exit 1
fi
git pull origin production
if test ! $? = 0; then
	exit 2
fi
