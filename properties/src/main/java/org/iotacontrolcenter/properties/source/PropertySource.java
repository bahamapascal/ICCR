package org.iotacontrolcenter.properties.source;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.iotacontrolcenter.dto.ActivityDto;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
import org.iotacontrolcenter.dto.NeighborDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;


public class PropertySource {

    private static PropertySource instance;
    private static Object SYNC_INST = new Object();
    private static final Pattern PATTERN_TRUE = Pattern.compile("1|on|true|yes", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_FALSE = Pattern.compile("0|off|false|no", Pattern.CASE_INSENSITIVE);
    private static final String CONF_FILE = "iccr.properties";
    public static final String ICCR_DIR_PROP = "iccrDir";

    public static final String ICCR_DIR_DEFAULT = "/opt/iccr";
    public static final String ICCR_API_KEY_PROP = "iccrApiKey";
    private static final String LOC_COUNTRY_PROP = "iccrCountryLocale";

    private static final String LOC_LANG_PROP = "iccrLanguageLocale";
    private static final String LOC_LANG_DEFAULT = "en";
    private static final String LOC_COUNTRY_DEFAULT = "US";
    public static final String ICCR_START_AT_START_PROP="iccrStartAtStartup";

    public static final String ICCR_START_IOTA_AT_START_PROP="iccrStartIotaAtStartup";
    public static final String ICCR_STOP_IOTA_AT_SHUTDOWN_PROP="iccrStopIotaAtShutdown";
    public static final String ICCR_PORT_NUMBER_PROP = "iccrPortNumber";
    public static final String IOTA_DLD_LINK_PROP="iotaDownloadLink";

    public static final String IOTA_APP_DIR_PROP = "iotaDir";

    public static final String IOTA_START_PROP = "iotaStartCmd";
    public static final String IOTA_PORT_NUMBER_PROP = "iotaPortNumber";
    public static final String IOTA_NBR_REFRESH_TIME_PROP = "iotaNeighborRefreshTime";

    public static final String IOTA_NEIGHBORS_PROP = "iotaNeighbors";
    public static final String IOTA_NEIGHBOR_PROP_PREFIX = "iotaNeighbor";
    public static final String ICCW_LANGUAGES_PROP = "iccwlanguages";

    public static final String ICCW_LANG_PREFIX = "iccwlanguage.";
    // The iccw localization properties are in files named: iccw_MessagesBundle_en.properties
    public static final String ICCW_LANG_FILE_PREFIX="iccw_MessagesBundle_";
    public static PropertySource getInstance() {
        synchronized (SYNC_INST) {
            if(PropertySource.instance == null) {
                PropertySource.instance = new PropertySource();
            }
            return PropertySource.instance;
        }
    }

    private Properties props;
    private String bakDir;
    private String binDir;
    private String confDir;
    private String confFile;
    private String dataDir;
    private String dldDir;
    private String iccrDir;
    private String logDir;
    private String osName;
    private String tmpDir;
    private PropertiesConfiguration propWriter;
    private DateTimeFormatter ymdhmsFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final Object SET_SYNC_OBJ = new Object();

    private PropertySource() {
        System.out.println("new PropertySource");

        osName = System.getProperty("os.name").toLowerCase();

        System.out.println("os.name: " + osName);

        iccrDir = System.getProperty(ICCR_DIR_PROP);
        if(iccrDir == null || iccrDir.isEmpty()) {
            System.out.println(ICCR_DIR_PROP + " system setting not available, using default: " + ICCR_DIR_DEFAULT);
            iccrDir = ICCR_DIR_DEFAULT;
        }

        bakDir = iccrDir + "/bak";
        binDir = iccrDir + "/bin";
        confDir = iccrDir + "/conf";
        dldDir = iccrDir + "/download";
        dataDir = iccrDir + "/data";
        tmpDir = iccrDir + "/tmp";
        logDir = iccrDir + "/logs";

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

    public String getApiKey() {
        return getString(ICCR_API_KEY_PROP);
    }

    public BitSet getBitSet(String key) {
        List<String> unparsed = getList(key);
        BitSet output;
        try {
            output = BitSet.valueOf(unparsed.stream().mapToLong(Long::parseLong).toArray());
        }
        catch(NumberFormatException e) {
            // If we couldn't parse the elements as Long, assume the property is empty or mangled
            output = new BitSet();
        }
        return output;
    }

    public ActivityDto getActivityDto(String key) {
        String unparsed = getString(key);
        ActivityDto output = new ActivityDto();

        try {
            output = ActivityDto.deserialize(unparsed);
        }
        catch (IOException e) {
            System.out
                    .println("PropertySource IOException loading ActivityDto: "
                    + e.getLocalizedMessage());
        }

        return output;
    }

    public void setActivityDto(String key, ActivityDto activity) {
        String serialized = "";

        try {

            serialized = activity.serialize();
        }
        catch (IOException e) {
            System.out
                    .println("PropertySource IOException saving RoaringBitmap: "
                            + e.getLocalizedMessage());
        }

        setProperty(key, serialized);

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

    public String getIccrBakDir() {
        return bakDir;
    }

    public String getIccrBinDir() {
        return binDir;
    }

    public String getIccrConfDir() {
        return confDir;
    }

    public String getIccrDataDir() {
        return dataDir;
    }

    public String getIccrDir() {
        return iccrDir;
    }

    public String getIccrDownloadDir() {
        return dldDir;
    }

    public String getIccrLogDir() {
        return logDir;
    }

    public String getIccrTmpDir() {
        return tmpDir;
    }

    public Properties getIccwLanguageChoices() {
        Properties props = new Properties();
        for(String k : getIccwLanguageKeys()) {
            props.setProperty(k, getString(ICCW_LANG_PREFIX + k));
        }
        return props;
    }

    public List<String> getIccwLanguageKeys() {
        return getList(ICCW_LANGUAGES_PROP);
    }

    public Properties getIccwLanguageProperties(String key) {
        String langFile = confDir + "/" + ICCW_LANG_FILE_PREFIX + key + ".properties";
        System.out.println("getIccwLanguageProperties: loading langFile: " + langFile);

        Properties props = new Properties();
        try {
            InputStream is = new FileInputStream(langFile);
            props.load(is);
        }
        catch(Exception e) {
            System.out.println("getIccwLanguageProperties: failed to load language localization properties from " + langFile);
            e.printStackTrace();
            throw new IllegalStateException("Failed to load " + key + " language localization properties");
        }
        return props;
    }

    public String getIccwLanguageProperty(String k) {
        return getString(ICCW_LANG_PREFIX + k);
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

    public float getFloat(String key) {
        String val = props.getProperty(key);
        if (val != null) {
            try {
                return Float.parseFloat(val);
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(
                        "Invalid float value provided for " + key);
            }
        }
        throw new IllegalArgumentException("No value provided for " + key);
    }

    public String getIotaAppDir() {
        return getString(IOTA_APP_DIR_PROP);
    }

    public String getIotaDownloadUrl() {
        return getString(IOTA_DLD_LINK_PROP);
    }

    public Integer getIotaNeighborRefreshTime() {
        try {
            return getInteger(IOTA_NBR_REFRESH_TIME_PROP);
        }
        catch(Exception e) {
            return 0;
        }
    }

    public IccrIotaNeighborsPropertyDto getIotaNeighbors() {
        synchronized(SET_SYNC_OBJ) {
            List<NeighborDto> nbrs = new ArrayList<>();
            for (String id : getNeighborKeys()) {
                try {
                    nbrs.add(new NeighborDto(
                            getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".key." + id),
                            getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".uri." + id),
                            getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".name." + id),
                            getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".descr." + id),
                            getBoolean(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".active." + id),
                            getActivityDto(
                                    PropertySource.IOTA_NEIGHBOR_PROP_PREFIX
                                            + ".activity." + id)
                            ));
                } catch (Exception e) {
                    System.out.println("getIotaNeighborsProperty exception: " + e.getLocalizedMessage());
                }
            }
            return new IccrIotaNeighborsPropertyDto(PropertySource.IOTA_NEIGHBORS_PROP, nbrs);
        }
    }

    public String getIotaStartCmd() {
        return getString(IOTA_START_PROP);
    }

    public String getIriJarFileInStartCmd() {
        // Something like: java -jar IRI.jar
        String iotaStartCmd = getIotaStartCmd();
        String jarFile = iotaStartCmd.replaceAll("^.*java.*-jar +", "");
        jarFile = jarFile.replaceAll("\\.jar.*$",".jar");
        return jarFile;
    }

    public String getIriJarFilePath() {
        return getIotaAppDir() + "/" + getIriJarFileInStartCmd();
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

    public String getLocaleCountry() {
        String val = getString(LOC_COUNTRY_PROP);
        if(val == null || val.isEmpty()) {
            val = LOC_COUNTRY_DEFAULT;
        }
        return val;
    }

    public String getLocaleLanguage() {
        String val = getString(LOC_LANG_PROP);
        if(val == null || val.isEmpty()) {
            val = LOC_LANG_DEFAULT;
        }
        return val;
    }

    public String getLocalIotaUrl() {
        return "http://localhost:" + getString(IOTA_PORT_NUMBER_PROP) + "/";
    }

    public List<String> getNeighborKeys() {
        return getList(IOTA_NEIGHBORS_PROP);
    }

    public String getNowDateTimestamp() {
        return ymdhmsFormatter.format(LocalDateTime.now());
    }

    public String getOsName() {
        return osName;
    }

    public List<String> getPropertyKeys() {
        List<String> keys = new ArrayList<>();
        //keys.add(ICCR_START_AT_START_PROP);
        //keys.add(ICCR_START_IOTA_AT_START_PROP);
        //keys.add(ICCR_STOP_IOTA_AT_SHUTDOWN_PROP);
        keys.add(ICCR_PORT_NUMBER_PROP);
        keys.add(IOTA_PORT_NUMBER_PROP);
        keys.add(IOTA_DLD_LINK_PROP);
        keys.add(IOTA_APP_DIR_PROP);
        keys.add(IOTA_START_PROP);
        keys.add(IOTA_NBR_REFRESH_TIME_PROP);
        return keys;
    }

    public String getString(String key) {
        return props.getProperty(key);
    }

    public void load() {
        try {
            InputStream is = new FileInputStream(confFile);
            props.load(is);
            is.close();
        }
        catch(Exception e) {
            System.out.println("failed to load iccr.properties from " + confDir);
            e.printStackTrace();
        }
    }

    public boolean osIsMac() {
        return (osName.indexOf("mac") >= 0);
    }

    public boolean osIsWindows() {
        return (osName.indexOf("win") >= 0);
    }

    public void removeProperty(String key) {
        props.remove(key);
        propWriter.clearProperty(key);
        try {
            propWriter.save();
        }
        catch(Exception e) {
            System.out.println("PropertySource remove prop exception saving PropertiesConfiguration: " + e.getLocalizedMessage());
        }
    }

    public void setIotaNeighborsConfig(IccrIotaNeighborsPropertyDto nbrs) {
        synchronized(SET_SYNC_OBJ) {
            // First clear existing neighbors
            clearNeighbors();

            String nbrKeys = "";
            String sep = "";
            Properties seenKeys = new Properties();
            String id;

            if (nbrs.getNbrs() != null) {
                for (NeighborDto nbr : nbrs.getNbrs()) {
                    System.out.println("updated neighbor: " + nbr);
                    id = nbr.getKey();
                    if (seenKeys.containsKey(id)) {
                        System.out.println("setIotaNeighborsConfig, ignoring duplicate nbr key: " + id);
                        continue;
                    }
                    seenKeys.setProperty(id, "true");
                    nbrKeys += sep + id;
                    if (sep.isEmpty()) {
                        sep = ",";
                    }
                    setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".key." + id, nbr.getKey());
                    setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".uri." + id, nbr.getUri());
                    if (nbr.getName() != null) {
                        setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".name." + id, nbr.getName());
                    }
                    else {
                        setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".name." + id, "");
                    }
                    if (nbr.getDescr() != null) {
                        setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".descr." + id, nbr.getDescr());
                    }
                    else {
                        setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".descr." + id, "");
                    }
                    setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".active." + id, String.valueOf(nbr.isActive()).toLowerCase());
                    if (nbr.getActivity() != null) {
                        setActivityDto(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX
                                + ".activity." + id, nbr.getActivity());
                    }
                    else {
                        setProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".activity." + id, "");
                    }
                }
            }
            System.out.println("Updated nbr keys: " + nbrKeys);
            setProperty(IOTA_NEIGHBORS_PROP, nbrKeys);
        }
    }

    public void setProperty(String key, Object value) {

        props.setProperty(key, (String)value);
        propWriter.setProperty(key, value);

        try {
            propWriter.save();
        }
        catch(Exception e) {
            System.out.println("PropertySource set prop exception saving PropertiesConfiguration: " + e.getLocalizedMessage());
        }
    }

    private void clearNeighbors() {
        List<String> nbrKeys = getNeighborKeys();
        for(String id : nbrKeys) {
            deleteNeighborById(id);
        }
    }

    private void deleteNeighborById(String id) {
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".key." + id);
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".uri." + id);
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".name." + id);
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".descr." + id);
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".active." + id);
        removeProperty(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".activity." + id);
    }

}
