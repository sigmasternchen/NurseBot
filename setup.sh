#!/bin/bash

directory="$1"

if test "$directory" = ""; then
	directory=$(pwd)
fi
directory=$(realpath "$directory")

pushd $directory

git clone "https://github.com/overflowerror/NurseBot" "git"

mkdir versions

mkdir -p git/build/NurseBot_lib

if test "$(ls git/build/NurseBot_lib)" = ""; then
	echo
	echo "Libs are missing."
	echo "Please copy them to $directory/git/build/NurseBot_lib"
	exit 1
fi

cd git/buildtools

./update.sh
./build.sh ../../
if test ! $? = 0; then
	echo "Build failed."
	popd
	exit 1
fi

version=$(./versions.sh ../../ | tail -n 1)
./deploy.sh ../../ $version

cd ../..

cp "$directory"/git/config.properties.default "$directory"/config.properties

ln -s "$directory"/git/buildtools/nursebot.sh ./
ln -s "$directory"/git/build/NurseBot_lib ./

popd
