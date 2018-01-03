package org.iotacontrolcenter.iccr.agent;

import org.iotacontrolcenter.iccr.agent.action.IccrAction;
import org.iotacontrolcenter.iccr.agent.action.RestartIccrAction;
import org.iotacontrolcenter.properties.locale.Localizer;

public class IccrActionFactory {

    public static final String RESTART = "restart";
    //public static final String START = "start";
    //public static final String STATUS = "status";
    //public static final String STOP = "stop";

    private static final String[] cmdList = {
            RESTART    // START, STATUS, STOP
    };

    public static IccrAction getAction(String cmd) {
        if (RESTART.equals(cmd)) {
            return new RestartIccrAction();
        }
        // Not there yet:
        /*
        else if (START.equals(cmd)) {
            return new StartIccrAction();
        }
        else if (STATUS.equals(cmd)) {
            return new StatusIccrAction();
        }
        else if (STOP.equals(cmd)) {
            return new StopIccrAction();
        }
        */
        throw new IllegalArgumentException(Localizer.getInstance().getFixedWithLocalText("IccrActionFactory (" + cmd + "): ", "unsupportedAction"));
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
