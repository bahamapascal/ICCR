package org.iotacontrolcenter.iccr.agent;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

public class IccrAgent {

    private static IccrAgent instance;
    private static Object SYNC_INST = new Object();
    public static IccrAgent getInstance() {
        synchronized (SYNC_INST) {
            if(IccrAgent.instance == null) {
                IccrAgent.instance = new IccrAgent();
            }
            return IccrAgent.instance;
        }
    }

    private Localizer localizer;
    private PropertySource propSource;

    private IccrAgent() {
        System.out.println("new ICCR Agent");
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
    }

    public ActionResponse action(String cmd, IccrPropertyListDto actionProps) {
        if(!IccrActionFactory.isValidAction(cmd)) {
            throw new IllegalArgumentException(localizer.getFixedWithLocalText("IccrAgent (" + cmd + "): ", "unsupportedAction"));
        }

        return IccrActionFactory.getAction(cmd).execute(actionProps);
    }

}
