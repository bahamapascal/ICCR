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

        if (AgentUtil.dirDoesNotExist(propSource.getIccrDir())) {
            throw new IllegalStateException("Missing directory: " + propSource.getIccrDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();

        System.out.println(ACTION_PROP + ", executing...");

        OsProcess proc = new IccrRestartProcess();
        boolean success = proc.start();

        String msg = localization.getLocalText("processSuccess");
        int rc = 0;
        if (!success) {
            if (proc.isStartError()) {
                System.out.println(ACTION_PROP + ", " + proc.getStartError());
                msg = proc.getStartError();
            }
            else {
                msg = localization.getLocalText("processFail");
            }
        }
        else {
            rc = proc.getResultCode();
            System.out.println(ACTION_PROP + ", " + proc.getName() + " " +
                    localization.getLocalText("processSuccess") + ", " +
                    localization.getLocalText("resultCode") + ": " + rc);
        }

        resp.setSuccess(success);
        resp.setMsg(msg);

        if(success) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));

        if(resp.isSuccess() &&
                resp.getProperty(ACTION_PROP) != null &&
                resp.getProperty(ACTION_PROP).valueIsSuccess()) {
            persistenceService.logIotaAction(PersistenceService.ICCR_RESTART, proc.getCmd(), "");
        }
        else {
            persistenceService.logIotaAction(PersistenceService.ICCR_RESTART_FAIL, proc.getCmd(), "");
        }

        return resp;
    }

}

