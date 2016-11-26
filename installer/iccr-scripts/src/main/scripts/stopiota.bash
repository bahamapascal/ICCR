#!/bin/bash

iccrDir=/opt/iccr
if [ ! -z "${1}" ]; then
   iccrDir=$1
fi

iotaDir=`grep iotaDir $iccrDir/conf/iccr.properties | sed -e 's/iotaDir=//g'`
iotaPidFile="${iotaDir}/iota.pid"

if [ ! -f "${iotaPidFile}" ]; then
    echo "IOTA PID file (${iotaPidFile}) not found"
    exit 0
fi

#useSudo="sudo "
useSudo=""

echo "cd $iotaDir"
cd $iotaDir

iotaPid=`cat $iotaPidFile`

echo "IOTA PID: $iotaPid"

stopCmd="${useSudo} kill ${iotaPid}"

echo "${stopCmd}"
$stopCmd

statusCode=$?

echo "statusCode: $statusCode"

echo "rm -f $iotaPidFile"
rm -f $iotaPidFile

exit $statusCode
