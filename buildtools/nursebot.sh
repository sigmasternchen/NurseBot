#!/bin/bash

SHUTDOWN=0
RESTART=1

INSTRUMENTATION=10

jar=NurseBot.jar

while true; do
	java -jar $jar
	ec=$?

	case $ec in
		$SHUTDOWN)
			echo "Got shutdown."
			break;;
		$RESTART)
			echo "Restart."
			continue;;
		*)
			echo "This should not happen!"
			break;;
	esac
done
