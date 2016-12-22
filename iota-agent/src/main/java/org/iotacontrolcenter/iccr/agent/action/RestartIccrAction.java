package org.iotacontrolcenter.iccr.agent.action;


import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.process.IccrRestartProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

public class RestartIccrAction extends AbstractIccrAction implements IccrAction {

    public static final String ACTION_PROP = "restartIccr";

    public RestartIccrAction() {
        super(new String[] { PropertySource.ICCR_DIR_PROP, PropertySource.IOTA_PORT_NUMBER_PROP });
    }

    @Override
    protected void validatePreconditions() {

        if (!AgentUtil.dirExists(propSource.getIccrDir())) {
            throw new IllegalStateException("Missing directory: " + propSource.getIccrDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();

        System.out.println(ACTION_PROP + ", executing...");

        OsProcess proc = new IccrRestartProcess();
        boolean rval = proc.start();

        String msg = localizer.getLocalText("processSuccess");
        int rc = 0;
        if (!rval) {
            if (proc.isStartError()) {
                System.out.println(ACTION_PROP + ", " + proc.getStartError());
                msg = proc.getStartError();
            }
            else {
                msg = localizer.getLocalText("processFail");
            }
        }
        else {
            rc = proc.getResultCode();
            System.out.println(ACTION_PROP + ", " + proc.getName() + " " +
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
            persister.logIotaAction(PersistenceService.ICCR_RESTART, proc.getCmd(), "");
        }
        else {
            persister.logIotaAction(PersistenceService.ICCR_RESTART_FAIL, proc.getCmd(), "");
        }

        return resp;
    }

}

