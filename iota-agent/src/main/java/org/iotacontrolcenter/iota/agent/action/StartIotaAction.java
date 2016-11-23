package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;

public class StartIotaAction implements IotaAction {

    @Override
    public ActionResponse execute() {
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }
}
