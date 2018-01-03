package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.action.*;
import org.iotacontrolcenter.properties.locale.Localizer;

import java.util.List;

public class ActionFactory {

    public static final String INSTALL = "install";
    public static final String START = "start";
    public static final String RESTART = "restart";
    public static final String STATUS = "status";
    public static final String STOP = "stop";
    public static final String DELETEDB = "deletedb";
    public static final String DELETE = "delete";
    public static final String NODEINFO = "nodeinfo";
    public static final String NEIGHBORS = "neighbors";
    public static final String ADDNEIGHBORS = "addNeighbors";
    public static final String REMOVENEIGHBORS = "removeNeighbors";

    private static final String[] cmdList = {
            INSTALL,
            START,
            RESTART,
            STATUS,
            STOP,
            DELETEDB,
            DELETE,
            NODEINFO,
            NEIGHBORS,
            ADDNEIGHBORS,
            REMOVENEIGHBORS
    };

    public static IotaAction getAction(String cmd) {
        if(INSTALL.equals(cmd)) {
            return new InstallIotaAction();
        }
        else if(START.equals(cmd)) {
            return new StartIotaAction();
        }
        else if(RESTART.equals(cmd)) {
            return new RestartIotaAction();
        }
        else if(STATUS.equals(cmd)) {
            return new StatusIotaAction();
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
        else if(NODEINFO.equals(cmd)) {
            return new NodeInfoIotaAction();
        }
        else if(NEIGHBORS.equals(cmd)) {
            return new NeighborsIotaAction();
        }
        else if(ADDNEIGHBORS.equals(cmd)) {
            return new AddNeighborsIotaAction();
        }
        else if(REMOVENEIGHBORS.equals(cmd)) {
            return new RemoveNeighborsIotaAction();
        }
        throw new IllegalArgumentException(Localizer.getInstance().getFixedWithLocalText("ActionFactory (" + cmd + "): ", "unsupportedAction"));
    }

    public static boolean isValidAction(String cmd) {
        if(cmd != null && !cmd.isEmpty()) {
            for(String s : cmdList) {
                if(s.equals(cmd)) {
                    return true;
                }
            }
        }
        return false;
    }

}
