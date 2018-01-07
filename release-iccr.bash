#!/bin/bash

#import common enviornment variables.
source global-variables.bash

if [ -d $dir/iccr-pre-${version} ]; then
    rm -rf $dir/iccr-pre-${version} > /dev/null 2>&1
fi

if [ ! -d $dist ]; then
    mkdir -p $dist
fi


if [ ! -d $dir ]; then
    sudo mkdir -p $dir
    sudo chown $user:$group $dir
fi

if [ -d $iccrdir ]; then
    mv $iccrdir $dir/iccr-pre-${version}
fi


if [ ! -d $iccrdir ]; then
    sudo mkdir -p $iccrdir
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

cd $dir

rm -f $dist/iccr-${version}.tgz  > /dev/null 2>&1

echo "Packaging iccr build env $dist/iccr-${version}.tgz iccr"
tar -czf $dist/iccr-${version}.tgz iccr

echo "cd -"
cd -

echo "Copying patch-iccr.bash to $dist"
cp patch-iccr.bash $dist

echo "Copying global-variables.bash to $dist"
cp global-variables.bash $dist

echo "Copying install-iccr.bash to $dist"
cp install-iccr.bash $dist

echo "Changing directory to $dist"
cd $dist

echo "Packaging iccr..."
tar -czf iccr-pkg-${version}.tgz iccr-${version}.tgz patch-iccr.bash install-iccr.bash global-variables.bash

echo "removing temporary directory..."
rm -rf $dir

