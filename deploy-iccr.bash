#!/bin/bash

cp packager/swarm/target/iccr-swarm.jar /opt/iccr/lib/iccr.jar
cp installer/property-files/src/main/resources/* /opt/iccr/conf
cp installer/iccr-scripts/src/main/scripts/* /opt/iccr/bin
#if [ ! -d /opt/iccr/lib/icc/src/main/webapp ]; then
#   mkdir -p /opt/iccr/lib/icc/src/main
#fi
#cp -r main/src/main/webapp /opt/iccr/lib/icc/src/main
cp -r ~/projects/extjs/iccw/build/production/icc /opt/iccr/lib
