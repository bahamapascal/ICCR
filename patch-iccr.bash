#!/bin/bash

ver=1.0.5
pkg=iccr-${ver}.tgz
dir=/opt
iccrdir=$dir/iccr
iccrbindir=$dir/iccr/bin
iccrlibdir=$dir/iccr/lib

archdir=`pwd`
tmpdir=/tmp/iccr-$ver

if [ ! -f $pkg ]; then
    echo "Failed to find the required ICCR archive file ($pkg) in the local directory!"
    echo "I can't continue"
    exit
fi

if [ ! -d $iccrdir ]; then
    echo "Existing ICCR install ($iccrdir) not found!"
    echo "I can not continue"
    exit
fi

echo "This script will patch the ICCR software that is in $iccrdir"
echo
echo "Make sure the ICCR process is halted by running $iccrdir/bin/iccr-ctl stop"
echo "Press enter..."

read junk

echo
echo "Extracting $pkg into temporary directory..."
echo
mkdir $tmpdir
cd $tmpdir
tar -xzf $archdir/$pkg

echo "Copying in place updated ICCR version $ver binary and script files..."
echo
echo "cp -f $tmpdir/iccr/bin/* $iccrbindir"
cp -fr $tmpdir/iccr/bin/* $iccrbindir

echo
echo "cp -fr $tmpdir/iccr/lib/* $iccrlibdir"
cp -fr $tmpdir/iccr/lib/* $iccrlibdir

cd -
rm -rf $tmpdir

echo
echo "Done"
