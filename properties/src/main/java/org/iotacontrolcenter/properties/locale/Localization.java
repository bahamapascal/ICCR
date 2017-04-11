package org.iotacontrolcenter.properties.locale;

import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {

    private static Localization instance;
    private static final Object SYNC_INST = new Object();
    public static Localization getInstance() {
        synchronized (SYNC_INST) {
            if(Localization.instance == null) {
                Localization.instance = new Localization();
            }
            return Localization.instance;
        }
    }

    private ResourceBundle defLocText;
    private final DateTimeFormatter eventDtf;
    private ResourceBundle locText;

    private Localization() {
        System.out.println("new Localization");

        PropertySource propSource = PropertySource.getInstance();

        String confDir = propSource.getIccrConfDir();

        String lang = propSource.getLocaleLanguage();
        String country = propSource.getLocaleCountry();
        Locale loc = new Locale(lang, country);

        eventDtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(loc);

        try {
            // Need to load the resource bundle prop file from the ICCR conf dir
            File file = new File(confDir);
            URL[] urls = {file.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);

            locText = ResourceBundle.getBundle("MessagesBundle", loc, loader);
            Locale defaultLoc = new Locale("en", "US");
            defLocText = ResourceBundle.getBundle("MessagesBundle", defaultLoc, loader);
        }
        catch(Exception e) {
            System.out.println("Localization exception: " + e.getLocalizedMessage());
        }
    }

    public String getEventTime() {
        return eventDtf.format(LocalDateTime.now());
    }

    public String getLocalTextWithFixed(String key, String fixed) {
        return getLocalText(key) + fixed;
    }

    public String getFixedWithLocalText(String fixed, String key) {
        return fixed + getLocalText(key);
    }

    public String getLocalText(String key) {
        String txt;
        try {
            txt = locText.getString(key);
            if (txt.isEmpty()) {
                txt = defLocText.getString(key);
                if (txt.isEmpty()) {
                    txt = key;
                }
            }
        }
        catch(Exception e) {
            System.out.println("getLocalText exception: " + e.getLocalizedMessage());
            txt = key;
        }
        return txt;
    }
}
