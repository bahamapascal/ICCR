bash install-iccr.bash
java -Xdebug -Djava.net.preferIPv4Stack=true -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=n -DiccrDir=/opt/iccr -Dswarm.https.port=14266 -jar /opt/iccr/lib/iccr.jar debug
