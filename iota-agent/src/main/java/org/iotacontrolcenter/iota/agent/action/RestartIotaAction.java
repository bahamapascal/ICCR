package org.iotacontrolcenter.iota.agent.action;


import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
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
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") +
                    ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();

        if (AgentUtil.isIotaActive()) {
            System.out.println("restartIota, first stopping");

            if(!AgentUtil.stopIota()) {
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

        // Pause for a sec to let it stop...
        try {
            Thread.sleep(1000);
        }
        catch(Exception e) {
        }

        System.out.println("restartIota, starting...");

        ActionResponse startResp = AgentUtil.startIota();
        if(startResp.isSuccess() &&
                startResp.getProperty(StartIotaAction.ACTION_PROP) != null &&
                startResp.getProperty(StartIotaAction.ACTION_PROP).valueIsSuccess()) {

            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
            resp.setSuccess(true);

            // Confusing to have restart event after start has logged
            /*
            persister.logIotaAction(PersistenceService.IOTA_RESTART,
                    propSource.getIotaStartCmd(),
                    "");
             */
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

}
