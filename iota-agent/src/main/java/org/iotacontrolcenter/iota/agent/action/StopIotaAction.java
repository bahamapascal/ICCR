package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.process.IotaStopProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;

public class StopIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "stopIota";

    public StopIotaAction() {
        super(null);
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) throws InterruptedException {
        System.out.println(ACTION_PROP);
        preExecute();

        /*
        ActionResponse resp = new ActionResponse();

        OsProcess proc = new IotaStopProcess();
        boolean success = proc.start();
        String msg = localization.getLocalText("processSuccess");
        int rc = 0;
        if (!success) {
            if (proc.isStartError()) {
                System.out.println(proc.getStartError());
                msg = proc.getStartError();
            }
            else {
                msg = localization.getLocalText("processFail");
            }
        } else {
            rc = proc.getResultCode();
            System.out.println(proc.getName() + " " +
                    localization.getLocalText("processSuccess") + ", " +
                    localization.getLocalText("resultCode") + ": " + rc);
        }
        */

        ActionResponse resp = doStop();

        if(resp.isSuccess()) {
            int maxTry = 0;
            while(AgentUtil.isIotaActive() && maxTry < 3) {
                // Pause for a bit to let it spin down
                Thread.sleep(2000);
                maxTry++;

                resp = doStop();
            }
        }

        /*
        resp.setSuccess(success);
        resp.setMsg(msg);

        if(success) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));
        */

        if(resp.isSuccess() &&
                resp.getProperty(ACTION_PROP) != null &&
                resp.getProperty(ACTION_PROP).valueIsSuccess()) {
            persistenceService.logIotaAction(PersistenceService.IOTA_STOP);
        }
        else {
            persistenceService.logIotaAction(PersistenceService.IOTA_STOP_FAIL,
                    "",
                    resp.getMsg());
        }

        return resp;
    }

    private ActionResponse  doStop() {
        ActionResponse resp = new ActionResponse();
        OsProcess proc = new IotaStopProcess();
        boolean success = proc.start();
        String msg = localization.getLocalText("processSuccess");
        int rc = 0;
        if (!success) {
            if (proc.isStartError()) {
                System.out.println(proc.getStartError());
                msg = proc.getStartError();
            }
            else {
                msg = localization.getLocalText("processFail");
            }
        }
        else {
            rc = proc.getResultCode();
            System.out.println(proc.getName() + " " +
                    localization.getLocalText("processSuccess") + ", " +
                    localization.getLocalText("resultCode") + ": " + rc);
        }

        resp.setSuccess(success);
        resp.setMsg(msg);

        if(success) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));

        return resp;
    }

}
