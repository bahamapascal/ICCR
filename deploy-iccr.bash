#!/bin/bash

ICC_PROD_DIR=~/projects/extjs/iccw/build/production/icc

cp packager/swarm/target/iccr-swarm.jar /opt/iccr/lib/iccr.jar
cp installer/property-files/src/main/resources/* /opt/iccr/conf
cp installer/iccr-scripts/src/main/scripts/* /opt/iccr/bin

if [ "${1}" = "icc" ]; then
    cp -r ${ICC_PROD_DIR} /opt/iccr/lib
fi
