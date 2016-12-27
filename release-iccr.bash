#!/bin/bash

if [ -z "${1}" ]; then
    echo "Pass version user  and group on command line"
    echo You are:
    id
    exit
fi

if [ -z "${2}" ]; then
    echo "Pass version user  and group on command line"
    echo You are:
    id
    exit
fi

if [ -z "${3}" ]; then
    echo "Pass version user  and group on command line"
    echo You are:
    id
    exit
fi

version=$1
user=$2
group=$3
dir=/opt
iccrdir=$dir/iccr
dist=~/projects/dist

if [ -d $dir/iccr-pre-${version} ]; then
    rm -rf $dir/iccr-pre-${version} > /dev/null 2>&1
fi

if [ ! -d $dir ]; then
    sudo mkdir $dir
    sudo chown $user:$group $dir
fi

if [ -d $iccrdir ]; then
    mv $iccrdir $dir/iccr-pre-${version}
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

./deploy-iccr.bash

if [ ! -d $dist ]; then
    mkdir $dist
fi

#cp changelog.txt $dist/icc-${version}-changelog.txt

cd $dir

rm -f $dist/iccr-${version}.tgz  > /dev/null 2>&1

tar -czf $dist/iccr-${version}.tgz iccr

# for immediate testing:
rm -rf iccr-${version}-dist > /dev/null 2>&1

mv iccr iccr-${version}-dist

tar -xzf $dist/iccr-${version}.tgz

sudo chown -R $user:$group iccr

