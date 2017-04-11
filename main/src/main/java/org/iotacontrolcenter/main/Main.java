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


// For WARArchive:
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.wildfly.swarm.undertow.WARArchive;
import java.io.File;

//import java.util.logging.Logger;

public class Main {

    public static boolean debug = false;
    public static boolean info = false;
    public static boolean noSsl = false;
    private static boolean doSwagger = true;

    //private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        // Must be instantiated after logging fraction:
        PropertySource propertySource;
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

        propertySource = PropertySource.getInstance();
        if(!noSsl) {
            System.out.println("Setting up SSL");
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
            //logger.info("No SSL");
            System.out.println("No SSL");
        }

        //logger.info("Starting container...");

        swarm.start();

        System.out.println("Deploying ICCR...");
        JAXRSArchive iccrWar = ShrinkWrap.create(JAXRSArchive.class, "iccr-app.war");
        iccrWar.addClass(IccrServiceImpl.class);
        iccrWar.addClass(NotFoundExceptionMapper.class);
        iccrWar.addAllDependencies();
        swarm.deploy(iccrWar);

        /*
        if(Main.doSwagger) {
            System.out.println("Deploying swagger...");
            SwaggerArchive archive = ShrinkWrap.create(SwaggerArchive.class, "iccr-swagger.war");
            JAXRSArchive deployment = archive.as(JAXRSArchive.class).addPackage(IccrService.class.getPackage());
            archive.setResourcePackages("org.iotacontrolcenter.api.IccrService");
            archive.setContextRoot("apidocs");
            deployment.addAllDependencies();
            swarm.deploy(deployment);
        }
        */

        //logger.info("Deploying ICC WAR...");
        File iccSrcdir = new File(propertySource.getIccrDir() + "/lib/icc");
        if(iccSrcdir.exists()) {
            System.out.println("Deploying ICC...");
            WARArchive iccWar = ShrinkWrap.create(WARArchive.class);
            iccWar.as(ExplodedImporter.class).importDirectory(iccSrcdir);
            iccWar.setContextRoot("icc");
            swarm.deploy(iccWar);
        }



        //logger.info("Done...");
    }
}
