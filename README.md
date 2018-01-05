# ICCR - IOTA Control Center Receiver



## Overview

ICCR is a java based manager and controller for Iota. It exposes restful APIs that allows management and control of an IOTA `IRI` process.     

## Build Instructions
These instructions will create a tgz file that can be unpacked and deployed onto a server.

*NOTE : Java 8 or above and maven required*
- Clone the iccr repo `$ git clone https://github.com/bahamapascal/ICCR.git`.
- change directory `$ cd iccr`.        
- Build sources `$ mvn package`.
- Install iccr in /opt/iccr `$ sudo ./release-iccr.bash`.  
- Deploy the generated file `iccr-pkg-<VERSION>.tgz` to your server.

## How to use?
- The ICCR is installed in /opt/iccr, it's controlled by the execution of the script `/opt/iccr/bin/iccr-ctl`,
which executes  the JAR file in `/opt/iccr/lib/iccr.jar`.
- The properties can be defined or altered in `/opt/iccr/conf/iccr.properties`.
- The iccr process logs in `/opt/iccr/logs`.
- It logs event data in CSV format to an audit file contained in `/opt/iccr/data`.
- It copies downloaded IOTA IRI files into `/opt/iccr/download`.
- It maintains backup copies of previous IOTA IRI file version in `/opt/iccr/bak`.

## Spawn ICCR in containers

*NOTE: docker and docker-compose must be installed*

`sudo docker-compose up -d` 

For further details please refer to the [wikis](https://github.com/bahamapascal/ICCR/wiki) page.

