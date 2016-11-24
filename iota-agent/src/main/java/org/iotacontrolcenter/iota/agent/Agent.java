package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.iota.agent.action.IotaAction;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

public class Agent {

    private static Agent instance;
    private static Object SYNC_INST = new Object();
    public static Agent getInstance() {
        synchronized (SYNC_INST) {
            if(Agent.instance == null) {
                Agent.instance = new Agent();
            }
            return Agent.instance;
        }
    }

    private Localizer localizer;
    private PropertySource propSource;

    private Agent() {
        System.out.println("new Iota Agent");
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
    }

    public ActionResponse action(String cmd) {
        if(!ActionFactory.isValidAction(cmd)) {
            throw new IllegalArgumentException(localizer.getFixedWithLocalText("IotaAgent (" + cmd + "): ", "unsupportedAction"));
        }

        return ActionFactory.getAction(cmd).execute();
    }


}
