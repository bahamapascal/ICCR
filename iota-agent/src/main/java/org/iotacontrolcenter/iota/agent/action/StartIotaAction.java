package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.process.IotaStartProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StartIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "startIota";

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

        // Do we need to have neighbors in order to start?
        if(false) {
            IccrIotaNeighborsPropertyDto nbrs = propSource.getIotaNeighbors();
            if (nbrs == null || nbrs.getNbrs().isEmpty()) {
                throw new IllegalStateException(localizer.getLocalText("missingProperty") + ": " + PropertySource.IOTA_NEIGHBORS_PROP);
            }
        }
    }

    @Override
    public ActionResponse execute() {
        preExecute();

        ActionResponse resp = new ActionResponse();

        OsProcess proc = new IotaStartProcess();
        boolean rval = proc.start();
        String msg = localizer.getLocalText("processSuccess");
        int rc = 0;
        if (!rval) {
            if (proc.isStartError()) {
                System.out.println(proc.getStartError());
                msg = proc.getStartError();
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

        return resp;
    }

}
