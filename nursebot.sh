#!/bin/bash

SHUTDOWN=1
RESTART=2

INSTRUMENTATION=10

jar=nursenoakes.jar

while true; do
	java -jar $jar
	ec=$?

	case $ec in
		$SHUTDOWN)
			break;;
		$RESTART)
			continue;;
		$INSTRUMENTATION)
			./instrumentation.sh $jar
			continue
			;;
		*)
			echo "This should not happen!"
	esac
done
