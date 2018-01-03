package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
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

        if (!AgentUtil.dirExists(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }

        if (!AgentUtil.fileExists(propSource.getIriJarFilePath())) {
            throw new IllegalStateException(localizer.getLocalText("missingFile") + ": " + propSource.getIriJarFilePath());
        }

        // Do we need to have neighbors in order to start?
        if(false) {
            IccrIotaNeighborsPropertyDto nbrs = propSource.getIotaNeighbors();
            if (nbrs == null || nbrs.getNbrs().isEmpty()) {
                throw new IllegalStateException(localizer.getLocalText("missingProperty") + ": " + PropertySource.IOTA_NEIGHBORS_PROP);
            }
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        this.actionProps = actionProps;
        preExecute();

        ActionResponse resp = new ActionResponse();

        if(AgentUtil.isIotaActive()) {
            System.out.println("startIota: already active");
            resp.setSuccess(true);
            resp.setMsg(localizer.getLocalText("startIotaAlreadyActive"));
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
            return resp;
        }

        OsProcess proc = new IotaStartProcess();
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

        if(rval) {
            // Pause for a sec to let it spin up
            try {
                Thread.sleep(2000);
            }
            catch(Exception e) {
            }

            boolean ok = addNeighbors();
            int nbrTry = 0;
            while(!ok && nbrTry++ < 5) {
                System.out.println("Failed to add neighbors, trying again...");
                try {
                    Thread.sleep(2000);
                }
                catch(Exception e) {
                }
                ok = addNeighbors();
            }
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
            persister.logIotaAction(PersistenceService.IOTA_START,
                    propSource.getIotaStartCmd(),
                    "");
        }
        else {
            persister.logIotaAction(PersistenceService.IOTA_START_FAIL,
                    propSource.getIotaStartCmd(),
                    resp.getMsg());
        }

        return resp;
    }

    private boolean addNeighbors() {
        System.out.println("started, adding neighbors...");

        AddNeighborsIotaAction addNbrs = new AddNeighborsIotaAction();
        ActionResponse resp = addNbrs.execute(actionProps);
        return resp.isSuccess() &&
                resp.getProperty(AddNeighborsIotaAction.ACTION_PROP) != null &&
                resp.getProperty(AddNeighborsIotaAction.ACTION_PROP).valueIsSuccess();
    }

}
