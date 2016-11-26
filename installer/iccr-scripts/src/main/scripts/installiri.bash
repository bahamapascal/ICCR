#!/bin/bash

if [ -z "${1}" ]; then
    echo "Missing required download file path as first argument";
    exit 1
fi
dldFilePath=$1

if [ -z "${2}" ]; then
    echo "Missing required IRI jar file name as second argument";
    exit 1
fi
iriFile=$2

iccrDir=/opt/iccr
if [ ! -z "${3}" ]; then
   iccrDir=$3
fi

iccrBakDir=`grep iccrBakDir $iccrDir/conf/iccr.properties | sed -e 's/iccrBakDir=//g'`
iriBakFile="${iccrBakDir}/${iriFile}"

iotaDir=`grep iotaDir $iccrDir/conf/iccr.properties | sed -e 's/iotaDir=//g'`
iriFilePath="${iotaDir}/${iriFile}"

dts=`date +%Y%m%d%H%M%S`

if [ ! -d $iotaDir ]; then
    echo "The IOTA directory (${iotaDir}) does not exist, fatal error."
    exit 1
fi

if [ -f $iriFilePath ]; then
    echo "Making backup copy of IOTA"
    echo "cp -f $iriFilePath ${iriBakFile}.${dts}"
    cp -f $iriFilePath ${iriBakFile}.${dts}
fi


echo "Copying into place the new downloaded IOTA"
echo "cp -f $dldFilePath $iriFilePath"
cp -f $dldFilePath $iriFilePath

exit $?
