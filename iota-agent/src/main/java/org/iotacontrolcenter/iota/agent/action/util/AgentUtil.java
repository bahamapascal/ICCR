package org.iotacontrolcenter.iota.agent.action.util;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.iota.agent.action.StartIotaAction;
import org.iotacontrolcenter.iota.agent.action.StatusIotaAction;
import org.iotacontrolcenter.iota.agent.action.StopIotaAction;

import java.io.File;

public class AgentUtil {

    public static boolean dirExists(String dirPath) {
        boolean rval = false;
        try {
            File f = new File(dirPath);
            rval = f.exists() && f.isDirectory();
        }
        catch(Exception e) {
            System.out.println("dirExists(" + dirPath + ") exception: " + e.getLocalizedMessage());
        }
        return rval;
    }

    public static boolean fileExists(String filePath) {
        boolean rval = false;
        try {
            File f = new File(filePath);
            rval = f.exists();
        }
        catch(Exception e) {
            System.out.println("fileExists(" + filePath + ") exception: " + e.getLocalizedMessage());
        }
        return rval;
    }

    public static void deleteFileQuietly(String filePath) {
        if(fileExists(filePath)) {
            try {
                File f = new File(filePath);
                f.delete();
            } catch (Exception e) {
            }
        }
    }

    public static  boolean stopIota() {
        StopIotaAction stopper = new StopIotaAction();
        ActionResponse resp = stopper.execute();
        return resp.isSuccess() &&
                resp.getProperty(StopIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StopIotaAction.ACTION_PROP).valueIsSuccess();
    }

    public static boolean isIotaActive() {
        StatusIotaAction status = new StatusIotaAction();
        ActionResponse resp = status.execute();
        return resp.isSuccess() &&
                resp.getProperty(StatusIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StatusIotaAction.ACTION_PROP).valueIsSuccess();
    }

    public static ActionResponse startIota() {
        StartIotaAction starter = new StartIotaAction();
        return starter.execute();
    }

    public static boolean startIotaBoolean() {
        StartIotaAction starter = new StartIotaAction();
        ActionResponse resp =  starter.execute();
        return resp.isSuccess() &&
                resp.getProperty(StartIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StartIotaAction.ACTION_PROP).valueIsSuccess();
    }
}
