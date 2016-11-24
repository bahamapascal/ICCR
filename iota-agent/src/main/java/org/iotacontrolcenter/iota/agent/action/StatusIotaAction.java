package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.process.IotaStatusProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StatusIotaAction extends AbstractAction implements IotaAction {

    public StatusIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    public ActionResponse execute() {
        preExecute();

        OsProcess proc = new IotaStatusProcess();
        boolean rval = proc.start();
        String msg = localizer.getLocalText("processSuccess");
        int rc = 0;
        if(!rval) {
            if(proc.isStartError()) {
                System.out.println(proc.getStartError());
                msg = proc.getStartError();
            }
        }
        else {
            rc = proc.getResultCode();
            System.out.println(proc.getName() + " " +
                    localizer.getLocalText("processSuccess") + ", " +
                    localizer.getLocalText("resultCode") + ": " + rc);
        }

        ActionResponse resp = new ActionResponse(rval, msg);
        if(rval) {
            resp.addProperty(new IccrPropertyDto("resultCode", rc));
        }
        return resp;
    }
}
