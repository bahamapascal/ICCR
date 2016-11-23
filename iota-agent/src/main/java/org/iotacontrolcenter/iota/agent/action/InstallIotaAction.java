package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;

public class InstallIotaAction implements IotaAction {

    @Override
    public ActionResponse execute() {
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }
}
