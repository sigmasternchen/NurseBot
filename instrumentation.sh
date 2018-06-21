#!/bin/bash

if test "$1" = ""; then
	export CLASSPATH="bin"

	for file in $(ls libs); do 
		export CLASSPATH=$CLASSPATH:libs/$file
	done

	java -cp $CLASSPATH -DoutputDirectory=bin org.javalite.instrumentation.Main

else
	export CLASSPATH="$1"

	java -cp $CLASSPATH -DoutputDirectory=./ org.javalite.instrumentation.Main
fi
