package org.iotacontrolcenter.persistence;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;

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
    public static final String IOTA_ADD_NBRS_FAIL = "addIotaNeighborsFail";

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

    private static final String IOTA_EVENT_FILE = "iota-event.csv";

    private Localizer localizer;
    private PropertySource propSource;
    private String iotaEventFilepath;

    private PersistenceService() {
        System.out.println("new PersistenceService");
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
        iotaEventFilepath = propSource.getIccrDataDir() + "/" + IOTA_EVENT_FILE;
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

            FileUtils.write(new File(iotaEventFilepath), line, true);
        }
        catch(IOException ioe) {
            System.out.println("logIotaAction, exception writing to file (" +
                        iotaEventFilepath + "): " +ioe.getLocalizedMessage());
        }
    }

}