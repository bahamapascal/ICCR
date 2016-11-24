package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.properties.source.PropertySource;

public class DeleteIotaAction extends AbstractAction implements IotaAction  {

    public DeleteIotaAction(PropertySource props) {
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
