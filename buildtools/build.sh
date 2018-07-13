#!/bin/bash

DEPLOY=$1
VERSIONS=./versions

EXIT_SUCCESS=0
EXIT_COMPILE_FAILED=1
EXIT_PACKING_FAILED=2
EXIT_INSTRUMENTATION_FAILED=3
EXIT_READY_FAILED=4

mkdir -p ../bin/
mkdir -p ../build/
mkdir -p ../build/NurseBot_lib/

pushd ../src/

for file in $(ls ../build/NurseBot_lib); do
	export CLASSPATH=$CLASSPATH:../build/NurseBot_lib/$file
done

echo "Building... "
javac -cp "$CLASSPATH" -d ../bin/ $(find ./ -iname "*.java")
if test ! $? = 0; then
	echo "... failed"
	exit $EXIT_COMPILE_FAILED
fi
echo "... done"

popd

pushd ../bin/

echo "Packing jar..."
jar cmf ../buildtools/MANIFEST.MF ../build/NurseBot.jar $(find ./ -iname "*.class")
if test ! $? = 0; then
	echo "... failed"
	exit $EXIT_PACKING_FAILED
fi
echo "... done"

popd

echo "Building instrumentation..."
./instrumentation.sh
if test ! $? = 0; then
	echo "... failed"
	exit $EXIT_INSTRUMENTATION_FAILED
fi
echo "... done"

echo "Copying instrumentation..."
cp ../bin/activejdbc_models.properties ../build/
if test ! $? = 0; then
	echo "... failed"
	exit $EXIT_INSTRUMENTATION_FAILED
fi
echo "... done"

pushd ../build
echo "Determine version..."
version=$(java -jar NurseBot.jar -v)
echo "This is version $version."

if test ! "$DEPLOY" = ""; then
	echo "Set to deploy..."
	cp NurseBot.jar $DEPLOY/$VERSIONS/NurseBot$version.jar
	if test ! $? = 0; then
		echo "... failed"
		exit $EXIT_READY_FAILED
	fi
	echo "... done"
fi

echo "Overall done."
popd

exit $EXIT_SUCCESS

