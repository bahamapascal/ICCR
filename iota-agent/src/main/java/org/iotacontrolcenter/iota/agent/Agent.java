package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.iota.agent.action.IotaAction;
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

    private PropertySource props = PropertySource.getInstance();

    private Agent() {
        System.out.println("new Iota Agent");
    }

    public ActionResponse action(String cmd) {
        if(!ActionFactory.isValidAction(cmd)) {
            System.out.println("Iota Agent: unsupported action: " + cmd);
            throw new IllegalArgumentException("Unsupported action: " + cmd);
        }

        IotaAction iAction = ActionFactory.getAction(cmd);
        return iAction.execute();
    }


}
