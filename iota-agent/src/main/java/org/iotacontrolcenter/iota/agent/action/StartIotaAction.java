package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StartIotaAction extends AbstractAction implements IotaAction {

    public StartIotaAction(PropertySource props) {
        super(props);
    }

    @Override
    public boolean setup() {
        return haveRequiredProperties();
    }

    @Override
    public ActionResponse execute() {
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }
}
