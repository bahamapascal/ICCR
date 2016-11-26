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

cd $iotaDir

iotaPid=`cat $iotaPidFile`

kill ${iotaPid}

statusCode=$?

rm -f $iotaPidFile

exit $statusCode
