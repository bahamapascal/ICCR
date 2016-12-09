#!/bin/bash

cp packager/swarm/target/iccr-swarm.jar /opt/iccr/lib/iccr.jar
cp installer/property-files/src/main/resources/* /opt/iccr/conf
cp installer/iccr-scripts/src/main/scripts/* /opt/iccr/bin
