#!/bin/bash

EXIT_SUCCESS=0
EXIT_COMPILE_FAILED=1
EXIT_PACKING_FAILED=2

mkdir -p ../bin/
mkdir -p ../build/
mkdir -p ../build/NurseBot_lib/

pushd ../src/

javac -cp $(find ../build/NurseBot_lib -iname "*.jar" | tr "\n" ":") -d ../bin/ $(find ./ -iname "*.java")
if test ! $? = 0; then
	exit $EXIT_COMPILE_FAILED
fi

popd

pushd ../bin/

jar cmf ../buildtools/MANIFEST.MF ../build/NurseBot.jar $(find ./ -iname "*.class")
if test ! $? = 0; then
	exit $EXIT_PACKING_FAILED
fi

popd

exit $EXIT_SUCCESS

