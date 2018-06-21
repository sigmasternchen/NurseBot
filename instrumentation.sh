#!/bin/bash

export CLASSPATH="bin"

for file in $(ls libs); do 
	export CLASSPATH=$CLASSPATH:libs/$file
done

java -cp $CLASSPATH -DoutputDirectory=bin org.javalite.instrumentation.Main
