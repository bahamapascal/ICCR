package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.action.*;
import org.iotacontrolcenter.properties.locale.Localization;

public class ActionFactory {

    public static final String INSTALL = "install";
    public static final String START = "start";
    public static final String RESTART = "restart";
    public static final String STATUS = "status";
    public static final String STOP = "stop";
    public static final String DELETE_DB = "deleteDb";
    public static final String DELETE = "delete";
    public static final String NODE_INFO = "nodeInfo";
    public static final String NEIGHBORS = "neighbors";
    public static final String ADD_NEIGHBORS = "addNeighbors";
    public static final String REMOVE_NEIGHBORS = "removeNeighbors";

    private static final String[] CMD_LIST = {
            INSTALL,
            START,
            RESTART,
            STATUS,
            STOP,
            DELETE_DB,
            DELETE,
            NODE_INFO,
            NEIGHBORS,
            ADD_NEIGHBORS,
            REMOVE_NEIGHBORS
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
        else if(DELETE_DB.equals(cmd)) {
            return new DeleteDbIotaAction();
        }
        else if(DELETE.equals(cmd)) {
            return new DeleteIotaAction();
        }
        else if(NODE_INFO.equals(cmd)) {
            return new NodeInfoIotaAction();
        }
        else if(NEIGHBORS.equals(cmd)) {
            return new NeighborsIotaAction();
        }
        else if(ADD_NEIGHBORS.equals(cmd)) {
            return new AddNeighborsIotaAction();
        }
        else if(REMOVE_NEIGHBORS.equals(cmd)) {
            return new RemoveNeighborsIotaAction();
        }
        throw new IllegalArgumentException(Localization.getInstance().getFixedWithLocalText("ActionFactory (" + cmd + "): ", "unsupportedAction"));
    }

    public static boolean isValidAction(String cmd) {
        if(cmd != null && !cmd.isEmpty()) {
            for(String s : CMD_LIST) {
                if(s.equals(cmd)) {
                    return true;
                }
            }
        }
        return false;
    }

}
