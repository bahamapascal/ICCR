package org.iotacontrolcenter.persistence;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    public static final String IOTA_DLD = "download";
    public static final String IOTA_DLD_FAIL = "downloadFail";
    public static final String IOTA_INSTALL = "install";
    public static final String IOTA_INSTALL_FAIL = "installFail";
    public static final String IOTA_STOP = "stop";
    public static final String IOTA_STOP_FAIL = "stopFail";
    public static final String IOTA_START = "start";
    public static final String IOTA_START_FAIL = "startFail";
    public static final String IOTA_RESTART = "restart";
    public static final String IOTA_RESTART_FAIL = "restartFail";
    public static final String IOTA_DELETE = "deleteIota";
    public static final String IOTA_DELETE_FAIL = "deleteIotaFail";
    public static final String IOTA_DELETE_DB = "deleteIotaDb";
    public static final String IOTA_DELETE_DB_FAIL = "deleteIotaDbFail";
    public static final String IOTA_ADD_NBRS_FAIL = "addIotaNeighbors";
    public static final String IOTA_ADD_NBRS = "addIotaNeighbors";
    public static final String IOTA_REMOVE_NBRS_FAIL = "removeIotaNeighborsFail";
    public static final String IOTA_REMOVE_NBRS = "removeIotaNeighbors";

    private static PersistenceService instance;
    private static Object SYNC_INST = new Object();
    public static PersistenceService getInstance() {
        synchronized (SYNC_INST) {
            if(PersistenceService.instance == null) {
                PersistenceService.instance = new PersistenceService();
            }
            return PersistenceService.instance;
        }
    }

    private static final String ICCR_IOTA_EVENT_FILE = "iota-event.csv";
    private static final String IOTA_LOG_FILE = "console.log";

    private Localizer localizer;
    private PropertySource propSource;
    private String iccrEventFilepath;
    private String iotaLogFilepath;

    private PersistenceService() {
        System.out.println("new PersistenceService");
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
        iccrEventFilepath = propSource.getIccrDataDir() + "/" + ICCR_IOTA_EVENT_FILE;
        iotaLogFilepath = propSource.getIotaAppDir() + "/" + IOTA_LOG_FILE;
    }

    public List<String> getIotaLog() throws IOException {
        File f = new File(iotaLogFilepath);

        //if(f.exists()) {
        try {
            return FileUtils.readLines(f);
        }
        catch(Exception e) {
            return new ArrayList<>();
        }
    }

    public List<String> getEventLog() throws IOException {
        File f = new File(iccrEventFilepath);

        //if(f.exists()) {
        try {
            return FileUtils.readLines(f);
        }
        catch(Exception e) {
            return new ArrayList<>();
        }
    }

    public void deleteEventLog() throws IOException {
        System.out.println("deleting Event log");
        FileUtils.deleteQuietly(new File(iccrEventFilepath));
    }

    public void logIotaAction(String event) {
        this.logIotaAction(event, "", "");
    }

    public void logIotaAction(String event, String data, String msg) {
        //System.out.println("logIotaAction : " + event);
        try {
            String line = localizer.getEventTime() + "," +  localizer.getLocalText(event) + "," + data;
            if(msg != null && !msg.isEmpty()) {
                line += "," + msg;
            }
            line += System.lineSeparator();

            //System.out.println(line);

            File f = new File(iccrEventFilepath);
            /*
            if(!f.exists()) {
                f.createNewFile();
            }
            */

            FileUtils.write(f, line, true);
        }
        catch(IOException ioe) {
            System.out.println("logIotaAction, exception writing to file (" +
                    iccrEventFilepath + "): " +ioe.getLocalizedMessage());
        }
    }

}