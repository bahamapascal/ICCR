package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.action.*;

public class ActionFactory {

    public static final String INSTALL = "install";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String DELETEDB = "deletedb";
    public static final String DELETE = "delete";

    public static IotaAction getAction(String cmd) {
        if(INSTALL.equals(cmd)) {
            return new InstallIotaAction();
        }
        else if(START.equals(cmd)) {
            return new StartIotaAction();
        }
        else if(STOP.equals(cmd)) {
            return new StopIotaAction();
        }
        else if(DELETEDB.equals(cmd)) {
            return new DeleteDbIotaAction();
        }
        else if(DELETE.equals(cmd)) {
            return new DeleteIotaAction();
        }
        throw new IllegalArgumentException("Unrecognized action: " +  cmd);
    }

    public static boolean isValidAction(String cmd) {
        return cmd != null &&
                !cmd.isEmpty() && (
                        cmd.equals(INSTALL) ||
                        cmd.equals(START) ||
                        cmd.equals(STOP) ||
                        cmd.equals(DELETEDB) ||
                        cmd.equals(DELETE) );
    }

}
