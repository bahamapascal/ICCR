#!/bin/bash

#version=1.0.3
version=$1

rm -rf /opt/iccr-pre-${version} > /dev/null 2>&1 

mv /opt/iccr /opt/iccr-pre-${version}
mkdir /opt/iccr
mkdir /opt/iccr/bak
mkdir /opt/iccr/bin
mkdir /opt/iccr/conf
mkdir /opt/iccr/data
mkdir /opt/iccr/download
mkdir /opt/iccr/lib
mkdir /opt/iccr/logs
mkdir /opt/iccr/tmp
chown -R dana:dana /opt/iccr
./deploy-iccr.bash

#cp changelog.txt ~/projects/dist/icc-${version}-changelog.txt

cd /opt
rm -f ~/projects/dist/iccr-${version}.tgz  > /dev/null 2>&1
tar -czf ~/projects/dist/iccr-${version}.tgz iccr


# for immediate testing:
rm -rf iccr-${version}-dist > /dev/null 2>&1 
mv iccr iccr-${version}-dist
tar -xzf ~/projects/dist/iccr-${version}.tgz
chown -R dana:dana iccr
