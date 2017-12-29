package org.iotacontrolcenter.iccr.agent;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.properties.locale.Localization;
import org.iotacontrolcenter.properties.source.PropertySource;

public class IccrAgent {

    private static IccrAgent instance;
    private static final Object SYNC_INST = new Object();
    public static IccrAgent getInstance() {
        synchronized (SYNC_INST) {
            if(IccrAgent.instance == null) {
                IccrAgent.instance = new IccrAgent();
            }
            return IccrAgent.instance;
        }
    }

    private final Localization localization;

    private IccrAgent() {
        System.out.println("new ICCR Agent");
        PropertySource propSource = PropertySource.getInstance();
        localization = Localization.getInstance();
    }

    public ActionResponse action(String cmd, IccrPropertyListDto actionProps) {
        if(!IccrActionFactory.isValidAction(cmd)) {
            throw new IllegalArgumentException(localization.getFixedWithLocalText("IccrAgent (" + cmd + "): ", "unsupportedAction"));
        }

        return IccrActionFactory.getAction(cmd).execute(actionProps);
    }

}
