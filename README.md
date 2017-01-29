# iccr
IOTA Control Center Receiver


1) Overview

The ICCR is a server process that functions as a microservice.  It provides a relaxed ReST (Representational State Transfer) API that allows for management and control of an IOTA (IRI) process on a single machine.

As a microservice, it:
i sa java based application that must have a java VM installed in order to run;
is a lightweight standalone executable that does not require any other framework to execute;
provides a well-defined API for client applications to target;
exposes a ReST API using provides standard HTTP operations based as the basis for control of an IOTA instance;
is a relaxed ReST server in that it does not fully adhere to the most stringent ReST guidelines;

2) Functionality

The ICCR:
is installed in /opt/iccr
is controlled by the execution of the script /opt/iccr/bin/iccr-ctl
executes library code contained in the JAR file /opt/iccr/lib/iccr.jar
is configured by properties defined in /opt/iccr/conf/iccr.properties
writes server output to a log file contained in /opt/iccr/logs
writes event data in CSV format to an audit file contained in /opt/iccr/data
copies downloaded IOTA IRI files into /opt/iccr/download
maintains backup copies of previous IOTA IRI file version in /opt/iccr/bak


2.a) Utility Scripts

/opt/iccr/bin/iccr-ctl:
is used to control the ICCR process
supports stop, start, restart, and status command line arguments

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


3) Configuration

iccr.properties
The ICCR process loads properties from the file /opt/iccr/conf/iccr.properties.

The following properties are available:
iccrLanguageLocale=en

iccrCountryLocale=US


iccrApiKey=secret
the ICCR API key value;
if this exact value is not in each client request HTTP header, then access will be refused;
each client request should have a header with this name: ICCR-API-KEY with its value being the API key

iccrDir=/opt/iccr

iccrDataDir=/opt/iccr/data

iccrConfDir=/opt/iccr/conf

iccrLogDir=/opt/iccr/logs

iccrLibDir=/opt/iccr/lib

iccrBinDir=/opt/iccr/bin

iccrTmpDir=/opt/iccr/tmp

iccrBakDir=/opt/iccr/bak

iccrDldDir=/opt/iccr/download

iccrLogLevel=DEBUG

iccrPortNumber=14266

iotaDownloadLink=http://85.93.93.110/iri-1.1.2.3.jar

iotaDir=/opt/iota

iotaStartCmd=java -jar IRI.jar -p

iotaPortNumber=14265


iotaNeighborRefreshTime=10
Time in minutes:

iotaNeighbors=
Specification of IOTA neighbors
First: a comma separated list of neighbor ID values, each neighbor ID is known as a key
Each key is a unique identifier of that neighbor
The key is used to specify a block of config properties for that neighbor
Second: one block of configuration properties for each neighbor
Each neighbor has 5 properties: key, name, descr, active, uri
Each neighbor configuration property block specifies those 5 properties using a mechanism that embeds the neighbor key in the property name
The pattern is:
iotaNeighbor.<propertyname>.<nbrkey>=<propertyvalue>
Example:
iotaNeighbors=al,bill
iotaNeighbor.key.al=al
iotaNeighbor.uri.al=udp://10.0.0.1:14265
iotaNeighbor.name.al=Al
iotaNeighbor.descr.al=Al node
iotaNeighbor.active.al=true
iotaNeighbor.key.bill=bill
iotaNeighbor.uri.bill=udp://10.0.0.2:14265
iotaNeighbor.name.bill=Bill
iotaNeighbor.descr.bill=Bill Node
iotaNeighbor.active.bill=true


Language Localization

The ICCR supports the generation of localized (i.e. English or German) text strings.

For the ICCR to emit messages in a desired language, two settings in iccr.properties must be set: a language code and a country code.

To have ICCR emit text in the desired language (German for example), you must:
1) set the desired language code as the value of the iccrLanguageLocale property in iccr.properties, for example: iccrLanguageLocale=de
2) set the iccrCountryLocale value in iccr.properties, for example:  iccrCountryLocale=DE
3) populate a MessagesBundle property file containing all the desired language text
4) if a language property file does not exist, create a new file with the language value and country value in the property file name, for example MessagesBundle_de_DE.properties
5) copy all the properties from the US English properties bundle file (MessagesBundle_en_US.properties) into the target language file, for example MessagesBundle_de_DE.properties
6) in the target language property file (i.e. MessagesBundle_de_DE.properties) replace all the English text with the appropriate translations


Possible values for language code (i.e. English, French, German, Spanish, Italian) are: en, fr, de, sp, it
See http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html


Secure Transport



4) API



