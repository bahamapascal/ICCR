package org.iotacontrolcenter.main;

import org.iotacontrolcenter.rest.resource.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;

public class Main {

    public static boolean debug = false;
    public static boolean info = false;
    public static boolean noSsl = false;

    public static void main(String[] args) throws Exception {

        if(args != null) {
            if(args.length > 0) {
                System.out.println("args[0] -> " + args[0]);
                noSsl = args[0].toLowerCase().equals("nossl");
                debug = args[0].toLowerCase().equals("debug");
                info = args[0].toLowerCase().equals("info");
            }
            if(args.length > 1) {
                System.out.println("args[1] -> " + args[1]);
                debug = args[1].toLowerCase().equals("debug");
                info = args[1].toLowerCase().equals("info");
            }
        }

        Swarm swarm = new Swarm();

        if(info) {
            swarm.fraction(LoggingFraction.createDefaultLoggingFraction());
        }
        else if(debug) {
            swarm.fraction(LoggingFraction.createDebugLoggingFraction());
        }
        else{
            swarm.fraction(LoggingFraction.createErrorLoggingFraction());
        }

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "iccr-app.war");
        deployment.addClass(IccrServiceImpl.class);
        deployment.addClass(NotFoundExceptionMapper.class);
        deployment.addAllDependencies();
        swarm.start().deploy(deployment);
    }
}
