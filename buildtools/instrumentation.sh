#!/bin/bash

pushd ../

if test "$1" = ""; then
	export CLASSPATH="bin"

	for file in $(ls build/NurseBot_lib); do 
		export CLASSPATH=$CLASSPATH:build/NurseBot_lib/$file
	done

	java -cp $CLASSPATH -DoutputDirectory=bin org.javalite.instrumentation.Main

else
	export CLASSPATH="$1"

	java -cp $CLASSPATH -DoutputDirectory=./ org.javalite.instrumentation.Main
fi
