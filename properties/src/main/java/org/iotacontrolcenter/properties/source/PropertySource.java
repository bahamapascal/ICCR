package org.iotacontrolcenter.properties.source;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertySource {

    private static PropertySource instance;
    private static Object SYNC_INST = new Object();
    public static PropertySource getInstance() {
        synchronized (SYNC_INST) {
            if(PropertySource.instance == null) {
                PropertySource.instance = new PropertySource();
            }
            return PropertySource.instance;
        }
    }

    private static final Pattern PATTERN_TRUE = Pattern.compile("1|on|true|yes", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_FALSE = Pattern.compile("0|off|false|no", Pattern.CASE_INSENSITIVE);
    private static final String CONF_FILE = "iccr.properties";
    private static final String CONF_DIR_PROP = "iccrConfDir";
    private static final String CONF_DIR_DEFAULT = "/opt/iccr/conf";

    public static final String ICCR_START_AT_START_PROP="iccrStartAtStartup";
    public static final String ICCR_START_IOTA_AT_START_PROP="iccrStartIotaAtStartup";
    public static final String ICCR_STOP_IOTA_AT_SHUTDOWN_PROP="iccrStopIotaAtShutdown";
    public static final String IOTA_PORT_NUMBER_PROP="iotaPortNumber";
    public static final String IOTA_START_PROP="iotaStartCmd";
    public static final String IOTA_NBR_REFRESH_PROP="iotaNeighborRefreshTime";
    public static final String IOTA_NEIGHBORS_PROP="iotaNeighbors";
    public static final String IOTA_NEIGHBOR_PROP_PREFIX="iotaNeighbor";

    private Properties props;
    private String confDir;
    private String confFile;
    private PropertiesConfiguration propWriter;

    private PropertySource() {
        System.out.println("creating new PropertySource");

        confDir = System.getProperty(CONF_DIR_PROP);
        if(confDir == null || confDir.isEmpty()) {
            System.out.println(CONF_DIR_PROP + " system setting not available, using default: " + CONF_DIR_DEFAULT);
            confDir = CONF_DIR_DEFAULT;
        }
        confFile = confDir + "/" + CONF_FILE;

        try {
            propWriter = new PropertiesConfiguration(confFile);
        }
        catch(Exception e) {
            System.out.println("PropertySource exception creating PropertiesConfiguration: " + e.getLocalizedMessage());
        }

        props = new Properties();
        load();
    }

    public void load() {
        try {
            InputStream is = new FileInputStream(confFile); // this.getClass().getResourceAsStream(confDir + "/" + CONF_FILE);
            props.load(is);
        }
        catch(Exception e) {
            System.out.println("failed to load iccr.properties from " + confDir);
            e.printStackTrace();
        }
    }

    public void setProperty(String key, Object value) {
        if(key.equals(IOTA_NEIGHBORS_PROP)) {
            setNeighbors(value);
        }
        else {
            props.setProperty(key, (String)value);
            propWriter.setProperty(key, value);

            try {
                propWriter.save();
            }
            catch(Exception e) {
                System.out.println("PropertySource exception saving PropertiesConfiguration: " + e.getLocalizedMessage());
            }
        }
    }

    private void setNeighbors(Object value) {

    }

    public List<String> getPropertyKeys() {
        List<String> keys = new ArrayList<>();
        keys.add(ICCR_START_AT_START_PROP);
        keys.add(ICCR_START_IOTA_AT_START_PROP);
        keys.add(ICCR_STOP_IOTA_AT_SHUTDOWN_PROP);
        keys.add(IOTA_PORT_NUMBER_PROP);
        keys.add(IOTA_START_PROP);
        keys.add(IOTA_NBR_REFRESH_PROP);
        return keys;
    }

    public List<String> getNeighborKeys() {
        return getList(IOTA_NEIGHBORS_PROP);
    }

    public String getString(String key) {
        return props.getProperty(key);
    }

    public boolean getBoolean(String key) {
        String val = props.getProperty(key);
        if(val != null) {
            if(PATTERN_TRUE.matcher(val).matches()) {
                return true;
            }
            else if(PATTERN_FALSE.matcher(val).matches()) {
                return false;
            }
            else {
                throw new IllegalArgumentException("Invalid boolean value provided for " + key);
            }
        }
        throw new IllegalArgumentException("No value provided for " + key);
    }

    public int getInteger(String key) {
        String val = props.getProperty(key);
        if(val != null) {
            try {
                return Integer.parseInt(val);
            }
            catch(NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid integer value provided for " + key);
            }
        }
        throw new IllegalArgumentException("No value provided for " + key);
    }

    public List<String> getList(String key) {
        List<String> keys = new ArrayList<>();
        String val = getString(key);
        if(val != null && !val.isEmpty()) {
            if(val.contains(",")) {
                String[] arr = val.split(",");
                for(String id : arr) {
                    keys.add(id.trim());
                }
            }
            else {
                keys.add(val);
            }
        }
        return keys;
    }

}