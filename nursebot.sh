#!/bin/bash

SHUTDOWN=1
RESTART=2

INSTRUMENTATION=10

while true; do
	java -jar nursenoakes.jar
	ec=$?

	case $ec in
		$SHUTDOWN)
			break;;
		$RESTART)
			continue;;
		$INSTRUMENTATION)
			./instrumentation.sh
			continue
			;;
		*)
			echo "This should not happen!"
	esac
done
