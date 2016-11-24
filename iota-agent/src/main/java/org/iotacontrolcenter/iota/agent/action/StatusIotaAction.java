package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StatusIotaAction extends AbstractAction implements IotaAction {

    public StatusIotaAction(PropertySource props) {
        super(props, new String[] { PropertySource.IOTA_APP_DIR_PROP });
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
