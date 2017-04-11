package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.properties.locale.Localization;
import org.iotacontrolcenter.properties.source.PropertySource;

public class Agent {

    private static Agent instance;
    private static final Object SYNC_INST = new Object();
    public static Agent getInstance() {
        synchronized (SYNC_INST) {
            if(Agent.instance == null) {
                Agent.instance = new Agent();
            }
            return Agent.instance;
        }
    }

    private final Localization localization;

    private Agent() {
        System.out.println("new Iota Agent");
        PropertySource propSource = PropertySource.getInstance();
        localization = Localization.getInstance();
    }

    public ActionResponse action(String cmd, IccrPropertyListDto actionProps) throws InterruptedException {
        if(!ActionFactory.isValidAction(cmd)) {
            throw new IllegalArgumentException(localization.getFixedWithLocalText("IotaAgent (" + cmd + "): ", "unsupportedAction"));
        }

        return ActionFactory.getAction(cmd).execute(actionProps);
    }


}
