package org.iotacontrolcenter.main;

import org.iotacontrolcenter.properties.source.PropertySource;
import org.iotacontrolcenter.rest.resource.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.management.SecurityRealm;
import org.wildfly.swarm.config.management.security_realm.SslServerIdentity;
import org.wildfly.swarm.config.management.security_realm.TruststoreAuthentication;
import org.wildfly.swarm.config.undertow.BufferCache;
import org.wildfly.swarm.config.undertow.HandlerConfiguration;
import org.wildfly.swarm.config.undertow.Server;
import org.wildfly.swarm.config.undertow.ServletContainer;
import org.wildfly.swarm.config.undertow.server.Host;
import org.wildfly.swarm.config.undertow.server.HttpsListener;
import org.wildfly.swarm.config.undertow.servlet_container.WebsocketsSetting;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.management.ManagementFraction;
import org.wildfly.swarm.undertow.UndertowFraction;

public class Main {

    public static boolean debug = false;
    public static boolean info = false;
    public static boolean noSsl = false;

    public static void main(String[] args) throws Exception {
        // Must be instantiated after logging fraction:
        PropertySource propertySource  = null;
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

        if(!noSsl) {
            propertySource = PropertySource.getInstance();
            System.out.println("setting up ssl");
            swarm.fraction(new ManagementFraction()
                    .securityRealm(new SecurityRealm("SSLRealm")
                            .sslServerIdentity(new SslServerIdentity<>()
                                    .keystorePath(propertySource.getIccrConfDir() + "/iccr-ks.jks")
                                    //.keystorePath("/opt/iccr/conf/iccr-ks.jks")
                                    .keystorePassword("secret")
                                    .alias("iccr")
                                    .keyPassword("secret")
                            )
                            .truststoreAuthentication(new TruststoreAuthentication()
                                    .keystorePath(propertySource.getIccrConfDir() + "/iccr-ts.jks")
                                    //.keystorePath("/opt/iccr/conf/iccr-ts.jks")
                                    .keystorePassword("secret")
                            )
                    ));

            swarm.fraction(new UndertowFraction()
                    .server(new Server("default-server")
                            .httpsListener(new HttpsListener("default")
                                    .securityRealm("SSLRealm")
                                    .socketBinding("https"))
                            .host(new Host("default-host")))
                    .bufferCache(new BufferCache("default"))
                    .servletContainer(new ServletContainer("default")
                            .websocketsSetting(new WebsocketsSetting()))
                    .handlerConfiguration(new HandlerConfiguration()));
        }
        else {
            System.out.println("no ssl");
        }

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "iccr-app.war");
        deployment.addClass(IccrServiceImpl.class);
        deployment.addClass(NotFoundExceptionMapper.class);
        deployment.addAllDependencies();
        swarm.start().deploy(deployment);
    }
}
