package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.process.IotaStartProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StartIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "startIota";

    private IccrPropertyListDto actionProps;

    public StartIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP,
                        PropertySource.IOTA_PORT_NUMBER_PROP,
                        PropertySource.IOTA_START_PROP});
    }

    @Override
    protected void validatePreconditions() {

        if (AgentUtil.dirDoesNotExist(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }

        if (!AgentUtil.fileExists(propSource.getIriJarFilePath())) {
            throw new IllegalStateException(localization.getLocalText("missingFile") + ": " + propSource.getIriJarFilePath());
        }

        // Do we need to have neighbors in order to start?
        /*
        if(false) {
            IccrIotaNeighborsPropertyDto iotaNeighbors = propSource.getIotaNeighbors();
            if (iotaNeighbors == null || iotaNeighbors.getNeighbors().isEmpty()) {
                throw new IllegalStateException(localization.getLocalText("missingProperty") + ": " + PropertySource.IOTA_NEIGHBORS_PROP);
            }
        }
        */
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) throws InterruptedException {
        this.actionProps = actionProps;
        preExecute();

        ActionResponse resp = new ActionResponse();

        if(AgentUtil.isIotaActive()) {
            System.out.println("startIota: already active");
            resp.setSuccess(true);
            resp.setMsg(localization.getLocalText("startIotaAlreadyActive"));
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
            return resp;
        }

        OsProcess proc = new IotaStartProcess();
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

        if(success) {
            // Pause for a sec to let it spin up
            Thread.sleep(2000);

            boolean ok = addNeighbors();
            int nbrTry = 0;
            while(!ok && nbrTry++ < 5) {
                System.out.println("Failed to add neighbors, trying again...");
                Thread.sleep(2000);
                ok = addNeighbors();
            }
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
            persistenceService.logIotaAction(PersistenceService.IOTA_START,
                    propSource.getIotaStartCmd(),
                    "");
        }
        else {
            persistenceService.logIotaAction(PersistenceService.IOTA_START_FAIL,
                    propSource.getIotaStartCmd(),
                    resp.getMsg());
        }

        return resp;
    }

    private boolean addNeighbors() {
        System.out.println("started, adding neighbors...");

        ActionResponse resp = new AddNeighborsIotaAction().execute(actionProps);
        return resp.isSuccess() &&
                resp.getProperty(AddNeighborsIotaAction.ACTION_PROP) != null &&
                resp.getProperty(AddNeighborsIotaAction.ACTION_PROP).valueIsSuccess();
    }

}
