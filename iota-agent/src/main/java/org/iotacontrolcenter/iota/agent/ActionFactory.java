package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.action.*;
import org.iotacontrolcenter.properties.source.PropertySource;

public class ActionFactory {

    public static final String INSTALL = "install";
    public static final String START = "start";
    public static final String STATUS = "status";
    public static final String STOP = "stop";
    public static final String DELETEDB = "deletedb";
    public static final String DELETE = "delete";

    public static IotaAction getAction(String cmd) {
        PropertySource props = PropertySource.getInstance();
        if(INSTALL.equals(cmd)) {
            return new InstallIotaAction();
        }
        else if(START.equals(cmd)) {
            return new StartIotaAction(props);
        }
        else if(STATUS.equals(cmd)) {
            return new StatusIotaAction(props);
        }
        else if(STOP.equals(cmd)) {
            return new StopIotaAction(props);
        }
        else if(DELETEDB.equals(cmd)) {
            return new DeleteDbIotaAction(props);
        }
        else if(DELETE.equals(cmd)) {
            return new DeleteIotaAction(props);
        }
        throw new IllegalArgumentException("Unrecognized action: " +  cmd);
    }

    public static boolean isValidAction(String cmd) {
        return cmd != null &&
                !cmd.isEmpty() && (
                        cmd.equals(INSTALL) ||
                        cmd.equals(START) ||
                        cmd.equals(STATUS) ||
                        cmd.equals(STOP) ||
                        cmd.equals(DELETEDB) ||
                        cmd.equals(DELETE) );
    }

}
