#!/bin/bash

iccrDir=/opt/iccr
if [ ! -z "${1}" ]; then
   iccrDir=$1
fi

iotaDir=`grep iotaDir $iccrDir/conf/iccr.properties | sed -e 's/iotaDir=//g'`
iotaStartCmd=`grep iotaStartCmd $iccrDir/conf/iccr.properties | sed -e 's/iotaStartCmd=//g'`
iotaPortNumber=`grep iotaPortNumber $iccrDir/conf/iccr.properties | sed -e 's/iotaPortNumber=//g'`
iotaPidFile=$iotaDir/iota.pid

#useSudo="sudo "
useSudo=""

useNohup="nohup "
#useNohup=

toConsoleLog=" > console.log 2>&1 "
toConsoleLog=""

startCmd="${useSudo} ${useNohup} ${iotaStartCmd} ${iotaPortNumber} ${toConsoleLog} &"

echo "cd $iotaDir"
cd $iotaDir

echo "${startCmd}"
$startCmd

statusCode=$?

iriPid=$!

echo "statusCode: $statusCode"
echo "PID: $iriPid"
echo $iriPid > $iotaPidFile

exit $statusCode
