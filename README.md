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
when starting, writes the ICCR process ID (PID) to the file /opt/iccr/iccr.pid
supports the following command line arguments to control the ICCR process:
stop
start
restart
status 
also supports following command line arguments when start is being done:
nossl
info
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


3) Configuration

The ICCR process reads configuration settings from the file /opt/iccr/conf/iccr.properties.

In addition, ICCR will also read language specific settings (i.e. language localization translation strings) from a file whose name starts with "MessagesBundle".

Both iccr.properties and the MessagesBundle files follow the Java properties file format, i.e. key=value

The following properties are available in the /opt/iccr/conf/iccr.properties:

iccrLanguageLocale

iccrCountryLocale


iccrApiKey
This specifies the value of ICCR API access key. If this exact value is not in each client request HTTP header, then that client request will be refused. Each client request should have a header with this name: ICCR-API-KEY with its value being the value specified by this "iccrApiKey" property.

iccrDir
This specifies the base ICCR installation directory, by default it is set to /opt/iccr

All of the following ICCR subdirectories are use:
iccrDataDir=/opt/iccr/data
iccrConfDir=/opt/iccr/conf
iccrLogDir=/opt/iccr/logs
iccrLibDir=/opt/iccr/lib
iccrBinDir=/opt/iccr/bin
iccrTmpDir=/opt/iccr/tmp
iccrBakDir=/opt/iccr/bak
iccrDldDir=/opt/iccr/download


iccrLogLevel
This property specifies the logging level used by the ICCR process. When set to DEBUG (iccrLogLevel=DEBUG), then ICCR will write more verbose output to the log file (the log file location is /opt/iccr/logs/iccr.log). The default value is DEBUG

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

# Example:
iotaNeighbors=david,johan
# The block of properties for neighbor "david":
iotaNeighbor.key.david=david
iotaNeighbor.uri.david=udp://192.0.0.1:14265
iotaNeighbor.name.david=David
iotaNeighbor.descr.david=David node
iotaNeighbor.active.david=true

# The block of properties for neighbor "johan":
iotaNeighbor.key.johan=johan
iotaNeighbor.uri.johan=udp://192.0.0.2:14265
iotaNeighbor.name.johan=Johan
iotaNeighbor.descr.johan=Johan Node
iotaNeighbor.active.johan=true


4) Language Localization

The ICCR supports the generation of localized (i.e. English or German) text strings.

For the ICCR to emit messages in a desired language, two settings in iccr.properties must be set: a language code and a country code.

To have ICCR emit text in the desired language (German for example), you must:
a) set the desired language code as the value of the iccrLanguageLocale property in iccr.properties, for example: iccrLanguageLocale=de
b) set the iccrCountryLocale value in iccr.properties, for example:  iccrCountryLocale=DE
c) populate a MessagesBundle property file containing all the desired language text
d) if a language property file does not exist, create a new file with the language value and country value in the property file name, for example MessagesBundle_de_DE.properties
e) copy all the properties from the US English properties bundle file (MessagesBundle_en_US.properties) into the target language file, for example MessagesBundle_de_DE.properties
f) in the target language property file (i.e. MessagesBundle_de_DE.properties) replace all the English text with the appropriate translations


Possible values for language code (i.e. English, French, German, Spanish, Italian) are: en, fr, de, sp, it
See http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html


5) Secure Transport

The ICCR supports secure encrypted traffic between ICCR and client applications over HTTPS. ICCR uses two PKI (Public Key Infrastructure) keystore files that are included with the ICCR distribution. PKI based security mechanisms depend on the idea of identity and trust. The first PKI file used by ICCR is a java formatted keystore (jks format) that establishes the identity of the ICCR server: /opt/iccr/conf/iccr-ks.jks. The iccr-ks.jks file is the "keystore" used by the ICCR.  The second PKI file used by ICCR is another java formatted keystore (jks format) file that establishes what clients the ICCR should trust:  /opt/iccr/iccr-ts.jks. The iccr-ts.jks file is the "truststore" used by ICCR.

Note that since the ICCR is a java based application, both files, iccr-ks.jks and iccr-ts.jks, utilize the java keystore format. The iccr-ks.jks file is the ICCR keystore that contains the PKI key that identities the ICCR's own identity. The second file, iccr-ts.jks, is a truststore that ICCR uses to decide which client connections to trust.

The ICCR truststore contains the public PKI certificate of the certificate authority (CA) that the ICCR trusts. The ICCR PKI keys are issued by a self-signed CA, created just to issue PKI certificates for the ICCR and the ICC GUI application. These self-signed certificates function strictly for the specific purpose of enabling secure encrypted transport between ICCR and the ICC.

As a client of the ICCR API, the ICC GUI application needs to communicate with the ICCR over secure HTTPS transport. The ICC itself has a keystore and truststore that enables that secure HTTPS layer with the ICCR. The ICC has a mirrored view of the ICCR. The ICC has a keystore and trusttore. The ICC's keystore contains a PKI key, issued by the same CA as the ICCR PKI key. The ICC's truststore contains the same CA as the ICCR's truststore. Thus the ICCR is able to trust the identity of the ICC (the ICC's key was issued by the CA that is in the ICCR truststore) and the ICC is able to trust the ICCR (the ICCR's key was issued by the CA that is in the ICC truststore).



5) API




