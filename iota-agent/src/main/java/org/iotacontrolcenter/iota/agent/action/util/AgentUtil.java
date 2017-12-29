package org.iotacontrolcenter.iota.agent.action.util;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.iota.agent.action.StartIotaAction;
import org.iotacontrolcenter.iota.agent.action.StatusIotaAction;
import org.iotacontrolcenter.iota.agent.action.StopIotaAction;

import java.io.File;

public class AgentUtil {

    public static boolean dirDoesNotExist(String dirPath) {
        File f = new File(dirPath);
        return !(f.exists() && !f.isDirectory());
    }

    public static boolean fileExists(String filePath) {
        File f = new File(filePath);
        return f.exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteFileQuietly(String filePath) {
        new File(filePath).delete();
    }

    public static  boolean stopIota() throws InterruptedException {
        StopIotaAction stopper = new StopIotaAction();
        ActionResponse resp = stopper.execute(null);
        return resp.isSuccess() &&
                resp.getProperty(StopIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StopIotaAction.ACTION_PROP).valueIsSuccess();
    }

    public static boolean isIotaActive() {
        StatusIotaAction status = new StatusIotaAction();
        ActionResponse resp = status.execute(null);
        return resp.isSuccess() &&
                resp.getProperty(StatusIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StatusIotaAction.ACTION_PROP).valueIsSuccess();
    }

    public static ActionResponse startIota() throws InterruptedException {
        StartIotaAction starter = new StartIotaAction();
        return starter.execute(null);
    }

    public static boolean startIotaBoolean() throws InterruptedException {
        StartIotaAction starter = new StartIotaAction();
        ActionResponse resp =  starter.execute(null);
        return resp.isSuccess() &&
                resp.getProperty(StartIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StartIotaAction.ACTION_PROP).valueIsSuccess();
    }
}
