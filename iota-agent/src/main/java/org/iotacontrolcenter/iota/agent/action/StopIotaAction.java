package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;

public class StopIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "stopIota";

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
