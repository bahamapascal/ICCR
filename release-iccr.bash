#!/bin/bash

if [ -z "${1}" ]; then
    echo "Pass user, and group on command line"
    echo You are:
    id
    exit
fi

if [ -z "${2}" ]; then
    echo "Pass user, and group on command line"
    echo You are:
    id
    exit
fi


version=1.0.0
user=$2
group=$3
dir=/opt
iccrdir=$dir/iccr
dist=$PWD/dist

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
    mkdir -p $dist
fi

#cp changelog.txt $dist/icc-${version}-changelog.txt

echo "cd $dir"
cd $dir

rm -f $dist/iccr-${version}.tgz  > /dev/null 2>&1

echo "tar -czf $dist/iccr-${version}.tgz iccr"
tar -czf $dist/iccr-${version}.tgz iccr

echo "cd -"
cd -

echo "cp patch-iccr.bash $dist"
cp patch-iccr.bash $dist

echo -n "Did you put the desired release version into install-iccr.bash? [Y/n] "
read yNo

echo "cp install-iccr.bash $dist"
cp install-iccr.bash $dist

echo "cd $dist"
cd $dist

echo "tar -czf iccr-pkg-${version}.tgz iccr-${version}.tgz patch-iccr.bash install-iccr.bash"
tar -czf iccr-pkg-${version}.tgz iccr-${version}.tgz patch-iccr.bash install-iccr.bash

echo "cd $dir"
cd $dir

# for immediate testing:
echo "rm -rf iccr-${version}-dist > /dev/null 2>&1"
rm -rf iccr-${version}-dist > /dev/null 2>&1

echo "mv iccr iccr-${version}-dist"
mv iccr iccr-${version}-dist

echo "tar -xzf $dist/iccr-${version}.tgz"
tar -xzf $dist/iccr-${version}.tgz

echo "sudo chown -R $user:$group iccr"
sudo chown -R $user:$group iccr



