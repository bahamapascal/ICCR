package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.process.IotaStopProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;

public class StopIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "stopIota";

    public StopIotaAction() {
        super(null);
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();

        OsProcess proc = new IotaStopProcess();
        boolean rval = proc.start();
        String msg = localizer.getLocalText("processSuccess");
        int rc = 0;
        if (!rval) {
            if (proc.isStartError()) {
                System.out.println(proc.getStartError());
                msg = proc.getStartError();
            }
            else {
                msg = localizer.getLocalText("processFail");
            }
        } else {
            rc = proc.getResultCode();
            System.out.println(proc.getName() + " " +
                    localizer.getLocalText("processSuccess") + ", " +
                    localizer.getLocalText("resultCode") + ": " + rc);
        }

        resp.setSuccess(rval);
        resp.setMsg(msg);

        if(rval) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));

        if(resp.isSuccess() &&
                resp.getProperty(ACTION_PROP) != null &&
                resp.getProperty(ACTION_PROP).valueIsSuccess()) {
            persister.logIotaAction(PersistenceService.IOTA_STOP);
        }
        else {
            persister.logIotaAction(PersistenceService.IOTA_STOP_FAIL,
                    "",
                    resp.getMsg());
        }

        return resp;
    }

}
