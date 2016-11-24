package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StopIotaAction extends AbstractAction implements IotaAction {

    public StopIotaAction() {
        super(null);
    }

    @Override
    public ActionResponse execute() {
        preExecute();
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }

}
