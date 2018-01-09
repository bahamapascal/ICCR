# ICCR - IOTA Control Center Receiver



## Overview

ICCR is a java based manager and controller for Iota. It exposes restful APIs that allows management and control of an IOTA `IRI` process.     

## Build Instructions
These instructions will create a tgz file that can be unpacked and deployed onto a server

*NOTE : Java 8 or above and maven required*
- Clone the iccr repo `$ git clone https://github.com/bahamapascal/ICCR.git`
- change directory `$ cd ICCR`
- Build sources `$ mvn package`
- Install iccr in /opt/iccr `$ sudo ./release-iccr.bash`
- Deploy the generated file `iccr-pkg-<VERSION>.tgz` to your server


## How to install and run ICCR
These instructions presume you have already built or downloaded the `iccr-pkg-<VERSION>.tgz` file.

*NOTE : Java 8 or above required*
- Extract the package archive `tar -xzf iccr-pkg-<VERSION>.tgz`
- For a first time install, run the install-iccr.bash script `./install-iccr.bash` and follow the setup
- For updating the ICCR, make sure ICCR is stopped, and then run patch-iccr.bash `./patch-iccr.bash`

Commands for starting/stopping the ICCR and other commands:
- Start ICCR `/opt/iccr/bin/iccr-ctl start`
- Start ICCR with ipv4 stack preference `/opt/iccr/bin/iccr-ctl start ipv4`
- Stop ICCR `/opt/iccr/bin/iccr-ctl stop`
- Restart ICCR `/opt/iccr/bin/iccr-ctl restart`
- Check ICCR status `/opt/iccr/bin/iccr-ctl status`
- For debugging, `debug` flag can be used with `tart` and `restart` commands

## Spawn ICCR in containers

*NOTE: docker and docker-compose must be installed*

- Execute the following command to spawn ICCR in containers `sudo docker-compose up -d`. 


## Metadata
- The ICCR is installed in /opt/iccr, it's controlled by the execution of the script `/opt/iccr/bin/iccr-ctl`,
which executes  the JAR file in `/opt/iccr/lib/iccr.jar`
- The properties can be defined or altered in `/opt/iccr/conf/iccr.properties`
- The iccr process logs in `/opt/iccr/logs`
- It logs event data in CSV format to an audit file contained in `/opt/iccr/data`
- It copies downloaded IOTA IRI files into `/opt/iccr/download`.
- It maintains backup copies of previous IOTA IRI file version in `/opt/iccr/bak`


For further details please refer to the [wikis](https://github.com/bahamapascal/ICCR/wiki) page.

