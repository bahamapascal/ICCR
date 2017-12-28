#!/bin/bash

ver=1.0.0-rc3
pkg=iccr-${ver}.tgz
dir=/opt
iccrDir=$dir/iccr
iccrPropDir=$iccrDir/conf
iccrPropFile=$iccrPropDir/iccr.properties
iccrBinDir=$dir/iccr/bin
iccrLibDir=$dir/iccr/lib
what=ICCR
mac=0
darwin=`uname | grep -i darwin`
if [ $darwin = "Darwin" ]; then
    mac=1
fi

archDir=`pwd`
tmpDir=/tmp/iccr-$ver

if [ ! -f $pkg ]; then
    echo "Failed to find the required $what archive file ($pkg) in the local directory!"
    echo "I can't continue"
    exit
fi

if [ ! -d $iccrDir ]; then
    echo "Existing $what install ($iccrDir) not found!"
    echo "I can not continue"
    exit
fi

echo "This script will patch the $what software that is in $iccrDir"
echo
echo "Make sure the $what process is halted by running $iccrDir/bin/iccr-ctl stop"
echo "Press enter..."

read junk

echo
echo "Extracting $pkg into temporary directory..."
echo
mkdir $tmpDir
cd $tmpDir
tar -xzf $archDir/$pkg

echo "Copying in place updated $what version $ver binary and script files..."
echo
echo "cp -f $tmpDir/iccr/bin/* $iccrBinDir"
cp -fr $tmpDir/iccr/bin/* $iccrBinDir

echo
echo "cp -fr $tmpDir/iccr/lib/* $iccrLibDir"
cp -fr $tmpDir/iccr/lib/* $iccrLibDir

cd -
rm -rf $tmpDir

###
# Prompt for permission to install new defaults:
###
prop=iotaDownloadLink
sVal="http:\/\/85.93.93.110\/iri-1.1.2.3.jar"
val="http://85.93.93.110/iri-1.1.2.3.jar"
curVal=`grep $prop $iccrPropFile | sed -e "s/${prop}=//g"`
if [ "${curVal}" != "${val}" ]; then
    echo
    echo There is a new default value for the $prop $what configuration property: $val
    echo Do you want that value to be inserted? [Y/n]
    read yN
    if [ -z "${yN}" ]; then
	yN=Y
    fi
    if [ "${yN}" = "Y" ]; then
        if [ "${mac}" = "1" ]; then
	    `sed -i '' "s/^${prop}=.*$/${prop}=${sVal}/g" $iccrPropFile`
	else
	    `sed -i "s/^${prop}=.*$/${prop}=${sVal}/g" $iccrPropFile`
	fi
    else
	echo Ok, leaving existing value: $curVal
    fi
fi

prop=iotaStartCmd
val="java -jar IRI.jar -p"
curVal=`grep $prop $iccrPropFile | sed -e "s/${prop}=//g"`
if [ "${curVal}" != "${val}" ]; then
    echo
    echo There is a new default value for the $prop $what configuration property: $val
    echo Do you want that value to be inserted? [Y/n]
    read yN
    if [ -z "${yN}" ]; then
	yN=Y
    fi
    if [ "${yN}" = "Y" ]; then
        if [ "${mac}" = "1" ]; then
	    `sed -i '' "s/^${prop}=.*$/${prop}=${val}/g" $iccrPropFile`
	else
	    `sed -i "s/^${prop}=.*$/${prop}=${val}/g" $iccrPropFile`
	fi
    else
	echo Ok, leaving existing value: $curVal
    fi
fi

echo
echo "Done"
