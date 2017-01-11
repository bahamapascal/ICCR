#!/bin/bash

if [ -z "${1}" ]; then
    echo "Error, need the previous version on the command line"
    echo
    echo "Usage: changeMavenVersion.bash <orig version> <new version>"
    echo
    echo "Example: ./changeMavenVersion.bash 2.2.27  2.2.27.1-SNAPSHOT"
    exit
fi

if [ -z "${2}" ]; then
    echo "Error, need the new version on the command line"
    echo
    echo "Usage: changeMavenVersion.bash <orig version> <new version>"
    echo
    echo "Example: ./changeMavenVersion.bash 2.2.27  2.2.27.1-SNAPSHOT"
    exit
fi

echo Changing $1 to $2

files=`find . -type f -name 'pom.xml' -print0 | xargs --null grep -il "<version>${1}</version>"`

for f in $files; do
    echo Changing $f
    sed -i "s/<version>${1}<\/version>/<version>${2}<\/version>/g" $f
done
