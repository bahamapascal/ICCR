#!/bin/sh

###
# This script is called by the ICCR service from a java environment
# when a user of the REST interface requests the ICCR service be restarted
###

APPLICATION=iccr

ICCR_DIR=/opt/iccr
ICCR_BIN_DIR=${ICCR_DIR}/bin
CTL_SCRIPT=${ICCR_BIN_DIR}/iccr-ctl

nohup $CTL_SCRIPT restart > /dev/null 2>&1 &

statusCode=$?

exit $statusCode



