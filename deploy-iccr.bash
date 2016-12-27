#!/bin/bash

ICC_PROD_DIR=~/projects/extjs/iccw/build/production/icc

if [ -z "${1}" ]; then
    echo "Pass user and group on command line"
    echo You are:
    id
    exit
fi

if [ -z "${2}" ]; then
    echo "Pass user and group on command line"
    echo You are:
    id
    exit
fi

user=$1
group=$2
dir=/opt
iccrdir=$dir/iccr

if [ ! -d $dir ]; then
    sudo mkdir $dir
    sudo chown $user:$group $dir
fi

if [ ! -d $iccrdir ]; then
    sudo mkdir $iccrdir
    sudo chown $user:$group $iccrdir
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
