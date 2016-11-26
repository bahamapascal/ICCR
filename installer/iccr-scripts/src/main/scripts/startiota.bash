#!/bin/bash

iccrDir=/opt/iccr
if [ ! -z "${1}" ]; then
   iccrDir=$1
fi

iotaDir=`grep iotaDir $iccrDir/conf/iccr.properties | sed -e 's/iotaDir=//g'`
iotaStartCmd=`grep iotaStartCmd $iccrDir/conf/iccr.properties | sed -e 's/iotaStartCmd=//g'`
iotaPortNumber=`grep iotaPortNumber $iccrDir/conf/iccr.properties | sed -e 's/iotaPortNumber=//g'`
iotaPidFile=$iotaDir/iota.pid

cd $iotaDir

startCmd="nohup ${iotaStartCmd} ${iotaPortNumber} > console.log 2>&1 &"
echo "${startCmd}"
nohup ${iotaStartCmd} ${iotaPortNumber}  > console.log 2>&1 &

statusCode=$?

iriPid=$!

echo $iriPid > $iotaPidFile

exit $statusCode
