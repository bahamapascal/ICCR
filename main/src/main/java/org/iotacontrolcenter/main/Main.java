package org.iotacontrolcenter.main;

import org.iotacontrolcenter.rest.resource.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;

public class Main {

    public static void main(String[] args) throws Exception {

        Swarm swarm = new Swarm();
        swarm.fraction(LoggingFraction.createDebugLoggingFraction() );

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "iccr-app.war");
        deployment.addClass(IccrServiceImpl.class);
        deployment.addClass(NotFoundExceptionMapper.class);
        deployment.addAllDependencies();
        swarm.start().deploy(deployment);
    }
}
