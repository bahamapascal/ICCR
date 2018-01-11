#!/bin/bash

#import common enviornment variables.
source global-variables.bash

ICC_PROD_DIR=~/projects/extjs/iccw/build/production/icc

if [ ! -d $dir ]; then
    mkdir -p $dir
    chown $user:$group $dir
fi

if [ ! -d $iccrdir ]; then
    mkdir -p $iccrdir
    chown $user:$group $iccrdir
    mkdir $iccrdir/bak
    mkdir $iccrdir/bin
    mkdir $iccrdir/conf
    mkdir $iccrdir/data
    mkdir $iccrdir/download
    mkdir $iccrdir/lib
    mkdir $iccrdir/logs
    mkdir $iccrdir/tmp
fi

cp packager/swarm/target/iccr-swarm.jar $iccrdir/lib/iccr.jar
cp installer/property-files/src/main/resources/* $iccrdir/conf
cp installer/iccr-scripts/src/main/scripts/* $iccrdir/bin

if [ "${3}" = "icc" ]; then
    cp -r ${ICC_PROD_DIR} $iccrdir/lib
fi
