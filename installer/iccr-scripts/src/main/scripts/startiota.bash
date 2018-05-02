#!/bin/bash

iccrDir=/opt/iccr
if [ ! -z "${1}" ]; then
   iccrDir=$1
fi

iotaDir=`grep iotaDir $iccrDir/conf/iccr.properties | sed -e 's/iotaDir=//g'`
iotaStartCmd=`grep iotaStartCmd $iccrDir/conf/iccr.properties | sed -e 's/iotaStartCmd=//g'`
iotaPortNumber=`grep iotaPortNumber $iccrDir/conf/iccr.properties | sed -e 's/iotaPortNumber=//g'`
iotaPidFile=$iotaDir/iota.pid
iotaOnBoot=`grep startIotaOnBoot $iccrDir/conf/iccr.properties | sed -e 's/startIotaOnBoot=//g'`

cd $iotaDir


if [ "$iotaOnBoot" = true ] ; then
    cronJob="cd $iotaDir && nohup ${iotaStartCmd} ${iotaPortNumber} > console.log 2>&1"
    echo "Creating cron job"
    if  crontab -l | grep -q "$cronJob"; then
	    echo "crontab already exists... Ignoring"
    else
        crontab -l | { cat; echo "@reboot $cronJob"; } | crontab -
    fi
fi


nohup ${iotaStartCmd} ${iotaPortNumber}  > console.log 2>&1 &

statusCode=$?

iriPid=$!

echo $iriPid > $iotaPidFile

exit $statusCode
