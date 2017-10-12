# ICCR - IOTA Control Center Receiver

## Build Instructions

These instructions will create a `tgz` file that can be unpacked and deployed onto a server.
* Install Java 8
* Install maven
* Open the command line to the directory with the ICCR source code
* `mvn package`
* `sudo ./release-iccr.bash <VERSION> <USER> <GROUP>` - where `<VERSION>` is the package version, and `<USER>` and `<GROUP>` are the user/group that will own the /opt/iccr directory
* Deploy the generated file, `iccr-pkg-<VERSION>.tgz`, to your server


## 1) Overview

The ICCR is a java based microservice server process.  It provides a ReST (Representational State Transfer) API that allows for management and control of an IOTA (IRI) process on a single machine. It functions as microservice by using the Wildfly Swarm framework to generate a single "fat" JAR file. It requires that java version 1.8 or greater be installed on the target machine.

The ICCR:
* is a single JAR file destributed in an TAR gzipped archive
* is distributed with control scripts to that manage its life-cycle (start, stop, status)
* is a lightweight standalone executable that does not require any other framework to execute
* exposes a ReST API using standard HTTP operations based as the basis for control of an IOTA instance
* supports HTTPS using self-signed PKI certificates
* supports authorized client usage only by means of an API access key


## 2) Functionality

The ICCR:
* is installed in /opt/iccr
* is controlled by the execution of the script /opt/iccr/bin/iccr-ctl
* executes library code contained in the JAR file /opt/iccr/lib/iccr.jar
* is configured by properties defined in /opt/iccr/conf/iccr.properties
* writes server output to a log file contained in /opt/iccr/logs
* writes event data in CSV format to an audit file contained in /opt/iccr/data
* copies downloaded IOTA IRI files into /opt/iccr/download
* maintains backup copies of previous IOTA IRI file version in /opt/iccr/bak


### 2.a) Directory Structure

/opt/iccr/data
The /opt/iccr/data directory contains a comma separted value (CSV) formattted file: iota-event.csv

The iota-event.csv file contains a record of the events that the ICCR executed when controlling and configuring the IOTA IRI process.

For example:
2017-01-11T07:19:49.926,download,http://85.93.93.110/iri-1.1.2.3.jar,/opt/iccr/download/iri-1.1.2.3.jar.20170111071940 (5475212 b
ytes)
2017-01-11T07:19:50.046,install,/opt/iccr/download/iri-1.1.2.3.jar.20170111071940,/opt/iota/IRI.jar
2017-01-11T07:19:52.225,IOTA addNeighbors,,Neighbors configuration is empty
2017-01-11T07:19:52.225,start,java -jar IRI.jar -p
2017-01-11T07:22:35.061,stop,
2017-01-11T07:23:19.239,IOTA addNeighbors,,Neighbors configuration is empty
2017-01-11T07:23:19.239,start,java -jar IRI.jar -p
2017-01-11T07:24:34.422,stop,


/opt/iccr/conf
The /opt/iccr/conf direcory contains the iccr.properties file and the ICCR PKI files that enable secure HTTPS transport (see below).


/opt/iccr/logs
The /opt/iccr/logs directory is the location where the ICCR log file (iccr.log) is written to.

/opt/iccr/lib
The /opt/iccr/lib directory is the location containing the single ICCR uber-JAR (java archive) file that is executed when the iccr-ctl script is used to start ICCR

/opt/iccr/bin
The /opt/iccr/bin directory contains scripts used by ICCR, see below for details.

/opt/iccr/tmp
The /opt/iccr/tmp directory is used by ICCR when downloading new versions of the ICCR IRI. Newly downloaded IRI files are first written to this temporary directory before being copied to the final /opt/iota.


/opt/iccr/bak
The /opt/iccr/bak directory contains backup copies of previous versions of the IOTA IRI file. After downloading a new version of the IOTA IRI file, ICCR will copy the previous (i.e. the current IRI in use) version into /opt/iccr/bak to provide for a disaster recovery possibility. ICCR will not automatically restore any previous IRI version, it only makes copies of previous versions to allwo the system administrator to manually execute a previous version if needed.

/opt/iccr/download
The /opt/iccr/download directory contains the newly downloaded copy of the IOTA IRI file while ICCR is downloading and installing a new version of the IOTA IRI. The newly downloaded IRI will be written into this directory, then copied into the configured IOTA directory.


### 2.b) Utility Scripts

/opt/iccr/bin/iccr-ctl:
is used to control the ICCR process
when starting, writes the ICCR process ID (PID) to the file /opt/iccr/iccr.pid
supports the following command line arguments to control the ICCR process:
stop
start
restart
status

also supports following command line arguments when start is being done:
nossl
debug

/opt/iccr/bin/checkiotastatus.bash:
is used by the ICCR to determine if the IOTA IRI java process is running
sets a status code of 0 if the IOTA IRI is running
sets a status code of 1 if the IOTA IRI is not running

/opt/iccr/bin/restarticcr.bash:
is used when the ICC application executes the ICCR ReST API operation to restart the ICCR
executes /opt/iccr/bin/iccr-ctl restart

/opt/iccr/bin/startiota.bash:
is used when the ICC application executes the ICCR ReST API operation to start the IOTA IRI
uses the IOTA start command and port number properties defined in /opt/iccr/conf/iccr.properties
stores the IOTA process ID (PID) to /opt/iota/iota.pid
redirects the IOTA IRI process output to /opt/iota/console.log


/opt/iccr/bin/stopiota.bash:
is used when the ICC application executes the ICCR ReST API operation to stop the IOTA IRI
uses the process ID in /opt/iota/iota.pid
sends a kill signal to the IOTA IRI process ID to allow for orderly shutdown


## 3) Configuration

ICCR reads configuration settings from the file /opt/iccr/conf/iccr.properties.

In addition, ICCR will also read language specific settings (i.e. language localization translation strings) from a file whose name starts with "MessagesBundle" (see Language Localization below).

The iccr.properties file follows the Java properties file format, i.e. key=value

The following properties are available in the /opt/iccr/conf/iccr.properties:

iccrLanguageLocale
See the Language Localization section below.

iccrCountryLocale
See the Language Localization section below.


iccrApiKey
This specifies the value of ICCR API access key. If this exact value is not in each client request HTTP header, then that client request will be refused. Each client request should have a header with this name: ICCR-API-KEY with its value being the value specified by this "iccrApiKey" property.

iccrDir
This specifies the base ICCR installation directory. By default it is set to /opt/iccr

iccrPortNumber
This property specifies the port number that ICCR will listen for incoming client API requests. It can be changed by a client application through the ICCR API. The default value is 14266 (iccrPortNumber=14266)


iotaDownloadLink
This property specifies the URL (hostname and path) to the location and path that the IOTA IRI jar file will be downloaded from. It can be changed by a client application through the ICCR API. The value of this property is used when a client application uses the ICCR API to download IOTA.
The current default value is iotaDownloadLink=http://85.93.93.110/iri-1.1.2.3.jar

iotaDir
This property specifies the location on the ICCR machine where the IOTA IRI is located. When IOTA IRI is downloaded, the IRI file will be copied into this directory. When the IRI process is started, its logging output will be written to the file /opt/iota/console.log. The default value is /opt/iota (iotaDir=/opt/iota).

iotaStartCmd
This property specifies the command string that will be used to execute the IOTA IRI. It can be changed by a client application through the ICCR API. The current default value is "java -jar IRI.jar -p" (iotaStartCmd=java -jar IRI.jar -p).

iotaPortNumber
This property specifies the port number that the IOTA IRI process will listen on.  It can be changed by a client application through the ICCR API. When ICCR starts the IOTA IRI process (using the iotaStartCmd property), the value of the iotaPortNumber property will be added to the end of the start command. The default value is 14265 (iotaPortNumber=14265)


iotaNeighborRefreshTime
This property specifies an interval of time in minutes at which the ICCR will refresh the list of neighbors being used by the running IOTA IRI process. It can be changed by a client application through the ICCR API.  Every interval of time specified by this property, the ICCR will remove and then re-add the currently configured list of neighbors. The default is 10 (minutes) (iotaNeighborRefreshTime=10)


iotaNeighbors
This property specifies set of neighbor nodes that will be added to the IOTA IRI process. Individual neighbor nodes can be added or removed by a client application through the ICCR API. When a client application adds a neighbor while IOTA is running, that new neighbor will be immediately added. If a client application removes a neighbor while IOTA is running, that new neighbor will be immediately removed from IOTA.

The value of the iotaNeighbors property is just a comma separated list of neighbor ID values. Each of these neighbor ID values are only used to generate second individual neighbor property names that specify the actual neighbor configuration.

Each neighbor ID value in the iotaNeighbors property list is a unique identifier of that neighbor. The ID is used to specify a block of configuration properties for that neighbor. There will be one block of configuration properties for each neighbor. Each neighbor configuration block has five properties: key, name, descr, active, and uri. Each neighbor configuration property block specifies those five properties using a mechanism that embeds the neighbor ID in the property name. The pattern for the individual neighbor property name is iotaNeighbor.<propertyname>.<ID>=<propertyvalue>

Example:

iotaNeighbors=david,johan
The block of properties for neighbor "david":
iotaNeighbor.key.david=david
iotaNeighbor.uri.david=udp://192.0.0.1:14265
iotaNeighbor.name.david=David
iotaNeighbor.descr.david=David node
iotaNeighbor.active.david=true

The block of properties for neighbor "johan":
iotaNeighbor.key.johan=johan
iotaNeighbor.uri.johan=udp://192.0.0.2:14265
iotaNeighbor.name.johan=Johan
iotaNeighbor.descr.johan=Johan Node
iotaNeighbor.active.johan=true


## 4) Language Localization

The ICCR supports the ability to emit localized (i.e. English, German, Spanish) text.

For the ICCR to emit messages in a desired language, two settings in iccr.properties must be set: a language code and a country code.

To have ICCR emit text in the desired language (German for example), you must:

a) set the desired language code as the value of the iccrLanguageLocale property in iccr.properties, for example: iccrLanguageLocale=de
b) set the iccrCountryLocale value in iccr.properties, for example:  iccrCountryLocale=DE
c) populate a MessagesBundle property file containing all the desired language text
d) if a language property file does not exist, create a new file with the language value and country value in the property file name, for example MessagesBundle_de_DE.properties
e) copy all the properties from the US English properties bundle file (MessagesBundle_en_US.properties) into the target language file, for example MessagesBundle_de_DE.properties
f) in the target language property file (i.e. MessagesBundle_de_DE.properties) replace all the English text with the appropriate translations


Possible values for language code (i.e. English, French, German, Spanish, Italian) are: en, fr, de, sp, it
For details, see http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html

Possible values for country code for English speaking countries (i.e. US, GreatBritain) are: US, GB, etc
Possible values for country code for German speaking countries (i.e. Germany, Austria, Switzerland) are: DE, AT, CH
For details, see http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html


## 5) Secure Transport

The ICCR supports secure encrypted traffic between ICCR and client applications over HTTPS. ICCR uses two PKI (Public Key Infrastructure) keystore files that are included with the ICCR distribution. PKI based security mechanisms depend on the idea of identity and trust. The first PKI file used by ICCR is a java formatted keystore (jks format) that establishes the identity of the ICCR server: /opt/iccr/conf/iccr-ks.jks. The iccr-ks.jks file is the "keystore" used by the ICCR.  The second PKI file used by ICCR is another java formatted keystore (jks format) file that establishes what clients the ICCR should trust:  /opt/iccr/iccr-ts.jks. The iccr-ts.jks file is the "truststore" used by ICCR.

Note that since the ICCR is a java based application, both files, iccr-ks.jks and iccr-ts.jks, utilize the java keystore format. The iccr-ks.jks file is the ICCR keystore that contains the PKI key that identities the ICCR's own identity. The second file, iccr-ts.jks, is a truststore that ICCR uses to decide which client connections to trust.

The ICCR truststore contains the public PKI certificate of the certificate authority (CA) that the ICCR trusts. The ICCR PKI keys are issued by a self-signed CA, created just to issue PKI certificates for the ICCR and the ICC GUI application. These self-signed certificates function strictly for the specific purpose of enabling secure encrypted transport between ICCR and the ICC.

As a client of the ICCR API, the ICC GUI application needs to communicate with the ICCR over secure HTTPS transport. The ICC itself has a keystore and truststore that enables that secure HTTPS layer with the ICCR. The ICC has a mirrored view of the ICCR. The ICC has a keystore and trusttore. The ICC's keystore contains a PKI key, issued by the same CA as the ICCR PKI key. The ICC's truststore contains the same CA as the ICCR's truststore. Thus the ICCR is able to trust the identity of the ICC (the ICC's key was issued by the CA that is in the ICCR truststore) and the ICC is able to trust the ICCR (the ICCR's key was issued by the CA that is in the ICC truststore).


## 5) API

### Overview

The root context path for all the ICCR ReST API resources is /iccr/rs.  All responses to HTTP GET operations return JSON objects. All HTTP PUT operations that update server side properties require the payload in JSON format. There are no HTTP POST operations that create new server side objects. The ICCR ReST API only uses HTTP POST operations as a means to initiate a longer running action that has intended side effects such as stopping or starting the IOTA IRI or downloading a new IOTA IRI version.

All access of the ICCR ReST API is subject to authorization. Each request to the API must have an HTTP header entry ICCR-API-KEY with the value set to the value of the iccrApiKey property in the iccr.properties file. If a given HTTP request does not have an ICCR-API-KEY or if the ICCR-API-KEY header value does not match the configured value of the iccr.properties iccrApiKey, the request will be refused with an HTTP unauthorized status code.

Usage notes:
* when the ICCR neighbors configuration property is updated, the updated neighbors will be automatically added to the IOTA IRI process;
* when the IOTA download operation is done, the IOTA IRI process will be automatically started after download completes;


The ICCR's ReST API is defined by the following resource paths:

### 5.a) Read ICCR configuration properties in bulk:

GET /iccr/rs/app/config

Example response:
{
    "properties":
     [
        {"key":"iccrPortNumber","value":"14266"},
        {"key":"iotaPortNumber","value":"14265"},
        {"key":"iotaDownloadLink","value":"http://85.93.93.110/iri-1.1.2.3.jar"},
        {"key":"iotaDir","value":"/opt/iota"},
        {"key":"iotaStartCmd","value":"java -jar IRI.jar -p"},
        {"key":"iotaNeighborRefreshTime","value":"10"}
    ]
}


### 5.b) Read individual ICCR configuration property values:

Single ICCR configuration property values can be read by adding the property name to the "app/config" ReST path when doing the GET operation.

GET /iccr/rs/app/config/iccrPortNumber

Example response:
{"key":"iccrPortNumber","value":"14266"}

/iccr/rs/app/config/iotaPortNumber

Example response:
{"key":"iotaPortNumber","value":"14265"}

/iccr/rs/app/config/iotaDownloadLink

Example response:
{"key":"iotaDownloadLink","value":"http://85.93.93.110/iri-1.1.2.3.jar"}

/iccr/rs/app/config/iotaDir

Example response:
{"key":"iotaDir","value":"/opt/iota"}

/iccr/rs/app/config/iotaStartCmd

Example response:
{"key":"iotaStartCmd","value":"java -jar IRI.jar -p"}

/iccr/rs/app/config/iotaNeighborRefreshTime

Example response:
{"key":"iotaNeighborRefreshTime","value":"10"}


### 5.c) Update individual ICCR configuration property values

Single ICCR configuration property values can be changed by doing an HTTP PUT operation to the app/config/{key} path: /iccr/rs/iccr/rs/app/config/{key}

The required payload for the PUT operation is a JSON object with two properties:
key
value

For example:
{"key":"iccrPortNumber","value":14266}

The response to the PUT operation is a JSON object indicating the success of the operation with two properties:
success
msg

For example:
{"success":true,"msg":"properties updated successfully"}

Example PUT operations and corresponding payloads:

/iccr/rs/app/config/iccrPortNumber
{"key":"iccrPortNumber","value":14266}

/iccr/rs/app/config/iotaPortNumber
{"key":"iotaPortNumber","value":14265}

/iccr/rs/app/config/iotaDownloadLink
{"key":"iotaDownloadLink","value":"http://85.93.93.110/iri-1.1.2.3.jar"}'

/iccr/rs/app/config/iotaDir
{"key":"iotaDir","value":"/opt/iota"}

/iccr/rs/app/config/iotaStartCmd
{"key":"iotaStartCmd","value":"java -jar IRI.jar -p"}

/iccr/rs/app/config/iotaNeighborRefreshTime
{"key":"iotaNeighborRefreshTime","value":"10"}


### 5.d) Read ICCR IOTA neighbor configuration properties in bulk:

The IOTA neighbors that are configured in the ICCR can be read in bulk by a dedicated ReST resource:

GET /iccr/rs/iccr/rs/app/config/iota/nbrs

Example response:
{
"key":"iotaNeighbors",
"nbrs":[
    {"active":true,"descr":"David Neighbor Node","key":"david","name":"nbr-David","uri":"udp://192.168.0.0.1:14265","numAt":0,"numIt":0,"numNt":0},
    {"active":true,"descr":"Johan Neighbor Node","key":"johan","name":"nbr-Johan","uri":"udp://192.168.0.0.2:14265","numAt":0,"numIt":0,"numNt":0},
    {"active":true,"descr":"Sebastian Neighbor Node","key":"sebastian","name":"nbr-sebastian","uri":"udp://192.168.0.0.3:14265","numAt":0,"numIt":0,"numNt":0}
    ]
}

### 5.e) Update the ICCR IOTA neighbor configuration

The ICCR IOTA neighbors property be updated in bulk by issuing a PUT to the app/config/iota/nbrs path:

PUT /iccr/rs/iccr/rs/app/config/iota/nbrs

The JSON object payload contains a nbrs property that is a list of IOTA neighbor objects. Each neighbor object has the following properties:
name
key
name
descr
uri
active

Example payload:
{
"key":"iotaNeighbors",
"nbrs":[
	{"key":"do1","name":"do1","descr":"do1","uri":"udp://123.237.239.166:14265","active":true},
	{"key":"do2","name":"do2","descr":"do2","uri":"udp://44.56.171.97:14265","active":true}
	]
}

5.e) Execute ICCR control commands

The ICCR ReST API supports a single operation that triggers the ICCR process to restart:

POST /iccr/rs/iccr/cmd/restart

Example response:
{
"success":true,
"msg":"process executed successfully",
"properties":[
		{ "key":"resultCode", "value":"0"},
		{ "key":"restartIccr","value":"true"}
	     ]
}

The ICCR ReST API does not currently support an operation to stop the ICCR process.

Here's an example of an attempt to do unsupported operation:
POST /iccr/rs/iccr/cmd/stop

Example response:
{
"success":false,
"msg":"IccrAgent (stop): command is not supported"
}


### 5.f) Execute IOTA IRI control commands

The ICCR ReST API supports operations that manage the IOTA IRI process running on the server.

Each IOTA control operation is executed by issuing an HTTP POST to a path of the form: /iccr/rs/iota/cmd/{action}

Supported actions are:
/iccr/rs/iota/cmd/start
/iccr/rs/iota/cmd/status
/iccr/rs/iota/cmd/stop
/iccr/rs/iota/cmd/restart
/iccr/rs/iota/cmd/deletedb
/iccr/rs/iota/cmd/delete
/iccr/rs/iota/cmd/install
/iccr/rs/iota/cmd/removeNeighbors
/iccr/rs/iota/cmd/addNeighbors

Each POST operation will return a JSON object that indicates the result of the operation (success) and a list of properties with additional details.

Examples:

POST /iccr/rs/iota/cmd/status
The response is a  JSON object, with the final operation name indicating the result of the operation: statusIota is true, IOTA is running:
{
"success":true,
"msg":"process executed successfully",
"properties":[
		{"key":"resultCode","value":"0"},
		{"key":"statusIota","value":"true"}
	     ]
}

POST /iccr/rs/iota/cmd/stop
In the JSON object response, the final operation name indicates the result of the operation: stopIota is true, IOTA was stopped:
{
"success":true,
"msg":"process executed successfully",
"properties": [
		{"key":"resultCode","value":"0"},
		{"key":"stopIota","value":"true"}
	      ]
}

POST /iccr/rs/iota/cmd/status
When IOTA is not running, the iota/cmd/status JSON response object will have the final "statusIota" operation name property indicating the result of the operation: statusIota is false, IOTA is not running:
{
"success":true,
"msg":"process executed successfully",
"properties":[
		{"key":"resultCode","value":"1"},
		{"key":"statusIota","value":"false"}
	     ]
}

See the command line examples section below for examples of the other iota/cmd operations.

### 5.g) Read IOTA IRI properties.

The ICCR ReST API supports two operations that read data from the IOTA IRI process without causing any side effects: nodeinfo and neighbors

The operation response is a JSON object whose "content" property contains the value received from the IOTA IRI process.

GET /iccr/rs/iota/cmd/nodeinfo

Example response:
{
"success":true,
"msg":"success",
"content": {
	"appName":"IRI",
	"appVersion":"1.1.2.3",
	"jreAvailableProcessors":2,
	"jreFreeMemory":51286960,
	"jreVersion":"1.8.0_111",
	"jreMaxMemory":883949568,
	"jreTotalMemory":60293120,
	"latestMilestone":"999999999999999999999999999999999999999999999999999999999999999999999999999999999",
	"latestMilestoneIndex":13250,
	"latestSolidSubtangleMilestone":"999999999999999999999999999999999999999999999999999999999999999999999999999999999",
	"latestSolidSubtangleMilestoneIndex":13250,
	"neighbors":2,
	"packetsQueueSize":0,
	"time":1485749319938,
	"tips":1,
	"transactionsToRequest":0,
	"duration":2
	}",
"properties":[
		{"key":"getIotaNodeInfo","value":"true"}
	     ]
}
	

GET /iccr/rs/iota/cmd/neighbors
Example response:
{
"success":true,
"msg":"success",
"content":"
	{"neighbors":
		[
		{"address":"234.237.239.166:14265","numberOfAllTransactions":0,"numberOfNewTransactions":0,"numberOfInvalidTransactions":0},
		{"address":"44.56.171.97:14265","numberOfAllTransactions":0,"numberOfNewTransactions":0,"numberOfInvalidTransactions":0}
		],
	"duration":1}",
"properties":[
		{"key":"getIotaNeighbors","value":"true"}
	     ]
}

If the iota/cmd/neighbors GET operation is done when IOTA is not active, the "success" property will be false in the JSON response.
Example response:
{
"success":false,
"msg":"IOTA application is not running",
"properties":[
		{"key":"getIotaNeighbors","value":"false"}
 	     ]
}

### 5.h) ICCR event log

The ICCR ReST API provides an operation to view the ICCR event log. The ICCR event log is a file in comma separated value (CSV) format that records the time of significant operations performed by the ICCR.

Each line contains:
event date
event type
event arguments
event details


GET /iccr/rs/iccr/rs/app/eventlog
Example response: n
[
"2017-01-29T20:54:38.956,restart ICCR,/opt/iccr/bin/restarticcr.bash",
"2017-01-29T21:09:58.515,stop,",
"2017-01-29T21:09:58.515,delete IOTA,",
"2017-01-29T21:54:56.885,download,http://85.93.93.110/iri-1.1.2.3.jar,/opt/iccr/download/iri-1.1.2.3.jar.20170129215453 (5476643 bytes)",
"2017-01-29T21:54:56.945,install,/opt/iccr/download/iri-1.1.2.3.jar.20170129215453,/opt/iota/IRI.jar",
"2017-01-29T21:54:59.06,IOTA addNeighbors,",
]

The ICCR ReST API also provides a DELETE operation delete the current ICCR event log:

DELETE /iccr/rs/iccr/rs/app/eventlog
Example response:
{
"success":true,
"msg":"Event log deleted"
}

### 5.i) IOTA IRI console log

The ICCR ReST API provides an operation to view the contents of the IOTA IRI console log file. This operation includes query parameters that may alter the behavior. The ReST resource path to view the IOTA IRI log file is: /iccr/rs/iota/log


The iota/log operation supports following query parameters:
fileDirection: either "head" (read from top or beginning) or "tail" (read from the end of the log file)
numLines: number of log file lines to return, default value is 500
lastFileLength: length of the log file as received in the previous iota/log response
lastFilePosition: position in the log file to begin returning content

GET /iccr/rs/iota/log?fileDirection=head

Example response:
{
"success":true,
"msg":"",
"lines":
[
"22:07:36.467 [main] INFO  com.iota.iri.IRI - Welcome to IRI 1.1.2.3",
"22:07:36.530 [main] ERROR com.iota.iri.IRI - Impossible to display logo. Charset UTF8 not supported by terminal.",
"22:07:36.558 [pool-2-thread-1] INFO  com.iota.iri.service.Node - Spawning Receiver Thread",
"22:07:36.564 [pool-2-thread-2] INFO  com.iota.iri.service.Node - Spawning Broadcaster Thread",
"22:07:36.567 [pool-2-thread-3] INFO  com.iota.iri.service.Node - Spawning Tips Requester Thread",
"22:07:36.569 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Spawning Neighbor DNS Refresher Thread",
"22:07:36.569 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Checking Neighbors' Ip...",
"22:07:36.816 [main] INFO  com.iota.iri.IRI - IOTA Node initialised correctly.",
"22:07:38.311 [XNIO-1 task-1] INFO  com.iota.iri.service.API - # 1 -> Requesting command 'addNeighbors'",
"22:08:13.389 [XNIO-1 task-2] INFO  com.iota.iri.service.API - # 2 -> Requesting command 'getNeighbors'"
]
"lastFilePosition":939,
"lastFileSize":2340}


GET /iccr/rs/iota/log?fileDirection=tail

Example response:
{
"success":true,
"msg":"",
"lines":
[
"22:37:36.570 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Checking Neighbors' Ip...",
"22:37:36.575 [pool-2-thread-4] INFO  com.iota.iri.service.Node - DNS Checker: Validating DNS Address 'sw-do1' with '104.236.239.166'",
"22:37:36.575 [pool-2-thread-4] INFO  com.iota.iri.service.Node - DNS Checker: Validating DNS Address 'sw-do2' with '45.55.171.97'",
"22:46:11.619 [XNIO-1 task-6] INFO  com.iota.iri.service.API - # 6 -> Requesting command 'removeNeighbors'",
"22:46:11.645 [XNIO-1 task-7] INFO  com.iota.iri.service.API - # 7 -> Requesting command 'addNeighbors'",
"22:48:22.536 [XNIO-1 task-8] INFO  com.iota.iri.service.API - # 8 -> Requesting command 'removeNeighbors'",
"22:49:10.021 [XNIO-1 task-9] INFO  com.iota.iri.service.API - # 9 -> Requesting command 'addNeighbors'",
"22:56:11.622 [XNIO-1 task-10] INFO  com.iota.iri.service.API - # 10 -> Requesting command 'removeNeighbors'",
"22:56:11.647 [XNIO-1 task-11] INFO  com.iota.iri.service.API - # 11 -> Requesting command 'addNeighbors'"
],
"lastFilePosition":2235,
"lastFileSize":2553}


## 6) Command line examples using curl

The ICCR ReST API may be queried using the command line utility program "curl". The following sections are examples of individual curl commands that access the various ICCR ReST resources.

Note the following usage of the curl command line options:
-k: tells curl not to perform validation of the server PKI certifiate (helpful when the server PKI certificate is self-signed)
-H: add the specified string (in key=value format) to the request's HTTP header 
-X PUT:  issue an HTTP PUT method
-X POST:  issue an HTTP POST method
-d <payload>: specifies the JSON object that will be the HTTP PUT payload

### 6.a) Examples of requests to GET the app/config properties:

GET /iccr/rs/app/config
curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config

GET /iccr/rs/app/config/{key}
curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iccrPortNumber

curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iotaPortNumber

curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iotaDownloadLink

curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iotaDir

curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iotaStartCmd

curl -k -H "ICCR-API-KEY:secret" https://localhost:14266/iccr/rs/app/config/iotaNeighborRefreshTime


### 6.b) Example of operations to update the various ICCR properties:

PUT /app/config/{key}:
curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iccrPortNumber","value":14266}' https://localhost:14266/iccr/rs/app/config/iccrPortNumber
{"success":true,"msg":"properties updated successfully"}

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaPortNumber","value":14265}' https://localhost:14266/iccr/rs/app/config/iotaPortNumber
{"success":true,"msg":"properties updated successfully"}


curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaDownloadLink","value":"http://85.93.93.110/iri-1.1.2.3.jar"}' https://localhost:14266/iccr/rs/app/config/iotaDownloadLink
{"success":true,"msg":"properties updated successfully"}

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaDir","value":"/opt/iota"}' https://localhost:14266/iccr/rs/app/config/iotaDir
{"success":true,"msg":"properties updated successfully"}

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaStartCmd","value":"java -jar IRI.jar -p"}' https://localhost:14266/iccr/rs/app/config/iotaStartCmd
{"success":true,"msg":"properties updated successfully"}

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaNeighborRefreshTime","value":10}' https://localhost:14266/iccr/rs/app/config/iotaNeighborRefreshTime
{"success":true,"msg":"properties updated successfully"}

Example of operations to query and update the ICCRI neighbor configuration.

GET /iccr/rs/iccr/rs/app/config/iota/nbrs:
{"key":"iotaNeighbors","nbrs":[{"active":true,"descr":"David Neighbor Node","key":"david","name":"nbr-David","uri":"udp://192.168.0.0.1:14265","numAt":0,"numIt":0,"numNt":0},{"active":true,"descr":"Johan Neighbor Node","key":"johan","name":"nbr-Johan","uri":"udp://192.168.0.0.2:14265","numAt":0,"numIt":0,"numNt":0},{"active":true,"descr":"Sebastian Neighbor Node","key":"sebastian","name":"nbr-sebastian","uri":"udp://192.168.0.0.3:14265","numAt":0,"numIt":0,"numNt":0}]}


PUT /iccr/rs/iccr/rs/app/config/iota/nbrs
curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X PUT -d '{"key":"iotaNeighbors","nbrs":[{"key":"do1","name":"do1","descr":"do1","uri":"udp://104.236.239.166:14265","active":true},{"key":"do2","name":"do2","descr":"do2","uri":"udp://45.55.171.97:14265","active":true}]}' https://localhost:14266/iccr/rs/app/config/iota/nbrs

{"success":true,"msg":"properties updated successfully"}


### 6.c) Example of operations to execute the various IOTA command

POST /iccr/rs/iccr/cmd/{action}

/iccr/rs/iccr/cmd/restart
curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iccr/cmd/restart
{"success":true,"msg":"process executed successfully","content":null,"properties":[{"key":"resultCode","value":"0"},{"key":"restartIccr","value":"true"}]}

/iccr/rs/iccr/cmd/stop
curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iccr/cmd/stop
{"success":false,"msg":"IccrAgent (stop): command is not supported"}


6.d) Example of operations to execute the various IOTA command actions:

POST /iccr/rs/iota/cmd/{action}

/iccr/rs/iota/cmd/stop:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/stop
{"success":true,"msg":"process executed successfully","content":null,"properties":[{"key":"resultCode","value":"0"},{"key":"stopIota","value":"true"}]}

/iccr/rs/iota/cmd/status:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/status
{"success":true,"msg":"process executed successfully","content":null,"properties":[{"key":"resultCode","value":"0"},{"key":"statusIota","value":"true"}]}

/iccr/rs/iota/cmd/start:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/start
{"success":true,"msg":"process executed successfully","content":null,"properties":[{"key":"resultCode","value":"0"},{"key":"startIota","value":"true"}]}

/iccr/rs/iota/cmd/restart:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/restart
{"success":true,"msg":null,"content":null,"properties":[{"key":"restartIota","value":"true"}]}


/iccr/rs/iota/cmd/deletedb:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/deletedb
{"success":true,"msg":"","content":null,"properties":[{"key":"deleteIotaDb","value":"true"}]}

/iccr/rs/iota/cmd/delete:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/delete
{"success":true,"msg":"","content":null,"properties":[{"key":"deleteIota","value":"true"}]}


/iccr/rs/iota/cmd/install:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/install
{"success":true,"msg":"success","content":null,"properties":[{"key":"installIota","value":"true"}]}


/iccr/rs/iota/cmd/removeNeighbors:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/removeNeighbors
{"success":true,"msg":"success","content":"{"removedNeighbors":2,"duration":1}","properties":[{"key":"removeIotaNeighbors","value":"true"}]}


/iccr/rs/iota/cmd/addNeighbors:

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/addNeighbors
{"success":true,"msg":"success","content":"{"addedNeighbors":2,"duration":0}","properties":[{"key":"addIotaNeighbors","value":"true"}]}


### 6.e) Example of operation to query the IOTA service for nodeinfo:

GET /iccr/rs/iota/cmd/nodeinfo

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" -X POST https://localhost:14266/iccr/rs/iota/cmd/nodeinfo
{"success":true,"msg":"success","content":"{"appName":"IRI","appVersion":"1.1.2.3","jreAvailableProcessors":2,"jreFreeMemory":51286960,"jreVersion":"1.8.0_111","jreMaxMemory":883949568,"jreTotalMemory":60293120,"latestMilestone":"999999999999999999999999999999999999999999999999999999999999999999999999999999999","latestMilestoneIndex":13250,"latestSolidSubtangleMilestone":"999999999999999999999999999999999999999999999999999999999999999999999999999999999","latestSolidSubtangleMilestoneIndex":13250,"neighbors":2,"packetsQueueSize":0,"time":1485749319938,"tips":1,"transactionsToRequest":0,"duration":2}","properties":[{"key":"getIotaNodeInfo","value":"true"}]}


### 6.f) Example of operation to view the IOTA log file with fileDirection=head

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" "https://localhost:14266/iccr/rs/iota/log?fileDirection=head&numLines=10"
{"success":true,
"msg":"",
"lines":
[
"22:07:36.467 [main] INFO  com.iota.iri.IRI - Welcome to IRI 1.1.2.3",
"22:07:36.530 [main] ERROR com.iota.iri.IRI - Impossible to display logo. Charset UTF8 not supported by terminal.",
"22:07:36.558 [pool-2-thread-1] INFO  com.iota.iri.service.Node - Spawning Receiver Thread",
"22:07:36.564 [pool-2-thread-2] INFO  com.iota.iri.service.Node - Spawning Broadcaster Thread",
"22:07:36.567 [pool-2-thread-3] INFO  com.iota.iri.service.Node - Spawning Tips Requester Thread",
"22:07:36.569 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Spawning Neighbor DNS Refresher Thread",
"22:07:36.569 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Checking Neighbors' Ip...",
"22:07:36.816 [main] INFO  com.iota.iri.IRI - IOTA Node initialised correctly.",
"22:07:38.311 [XNIO-1 task-1] INFO  com.iota.iri.service.API - # 1 -> Requesting command 'addNeighbors'",
"22:08:13.389 [XNIO-1 task-2] INFO  com.iota.iri.service.API - # 2 -> Requesting command 'getNeighbors'"
]
"lastFilePosition":939,
"lastFileSize":2340
}


### 6.g) Example of operation to view the IOTA log file with fileDirection=tail and numLines=10

curl -k -H "ICCR-API-KEY:secret" -H "Content-Type:application/json" "https://localhost:14266/iccr/rs/iota/log?fileDirection=tail&numLines=10"
{"success":true,
"msg":"",
"lines":
[
"22:37:36.570 [pool-2-thread-4] INFO  com.iota.iri.service.Node - Checking Neighbors' Ip...",
"22:37:36.575 [pool-2-thread-4] INFO  com.iota.iri.service.Node - DNS Checker: Validating DNS Address 'sw-do1' with '106.237.239.166'",
"22:37:36.575 [pool-2-thread-4] INFO  com.iota.iri.service.Node - DNS Checker: Validating DNS Address 'sw-do2' with '44.57.171.97'",
"22:46:11.619 [XNIO-1 task-6] INFO  com.iota.iri.service.API - # 6 -> Requesting command 'removeNeighbors'",
"22:46:11.645 [XNIO-1 task-7] INFO  com.iota.iri.service.API - # 7 -> Requesting command 'addNeighbors'",
"22:48:22.536 [XNIO-1 task-8] INFO  com.iota.iri.service.API - # 8 -> Requesting command 'removeNeighbors'",
"22:49:10.021 [XNIO-1 task-9] INFO  com.iota.iri.service.API - # 9 -> Requesting command 'addNeighbors'",
"22:56:11.622 [XNIO-1 task-10] INFO  com.iota.iri.service.API - # 10 -> Requesting command 'removeNeighbors'",
"22:56:11.647 [XNIO-1 task-11] INFO  com.iota.iri.service.API - # 11 -> Requesting command 'addNeighbors'"
],
"lastFilePosition":2235,
"lastFileSize":2553
}






