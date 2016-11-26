package org.iotacontrolcenter.iota.agent.action;


import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

public class RestartIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "restartIota";

    public RestartIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP,
                PropertySource.IOTA_PORT_NUMBER_PROP,
                PropertySource.IOTA_START_PROP});
    }

    @Override
    protected void validatePreconditions() {

        if (!AgentUtil.dirExists(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute() {
        preExecute();

        ActionResponse resp = new ActionResponse();

        if (isActive()) {
            System.out.println("restartIota, first stopping");
            if(!stopIt()) {
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("stopIotaFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localizer.getLocalText("stopIotaFail"));

                persister.logIotaAction(PersistenceService.IOTA_STOP_FAIL,
                        "",
                        resp.getMsg());

                return resp;
            }
        }

        System.out.println("restartIota, starting...");
        // Pause for a sec to let it stop...
        try {
            Thread.sleep(1000);
        }
        catch(Exception e) {

        }
        ActionResponse startResp = startIt();
        if(startResp.isSuccess() &&
                startResp.getProperty(StartIotaAction.ACTION_PROP) != null &&
                startResp.getProperty(StartIotaAction.ACTION_PROP).valueIsSuccess()) {

            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
            resp.setSuccess(true);

            persister.logIotaAction(PersistenceService.IOTA_RESTART,
                    propSource.getIotaStartCmd(),
                    "");
        }
        else {
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            resp.setSuccess(false);
            resp.setMsg(startResp.getMsg());

            persister.logIotaAction(PersistenceService.IOTA_RESTART_FAIL,
                    "",
                    "");
        }

        return resp;
    }

    private ActionResponse startIt() {
        StartIotaAction starter = new StartIotaAction();
        return starter.execute();
    }

    private boolean stopIt() {
        StopIotaAction stopper = new StopIotaAction();
        ActionResponse resp = stopper.execute();
        return resp.isSuccess() &&
                resp.getProperty(StopIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StopIotaAction.ACTION_PROP).valueIsSuccess();
    }


    private boolean isActive() {
        StatusIotaAction status = new StatusIotaAction();
        ActionResponse resp = status.execute();
        return resp.isSuccess() &&
                resp.getProperty(StatusIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StatusIotaAction.ACTION_PROP).valueIsSuccess();
    }

}
