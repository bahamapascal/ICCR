package org.iotacontrolcenter.iota.agent.action;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;

public class DeleteIotaAction extends AbstractAction implements IotaAction  {

    public static final String ACTION_PROP = "deleteIota";

    public DeleteIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP });
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

        boolean rval = true;
        String msg = "";

        if (AgentUtil.isIotaActive()) {
            System.out.println("deleteIota, first stopping");
            if(!AgentUtil.stopIota()) {
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("deleteIotaFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localizer.getLocalText("deleteIotaFail"));

                persister.logIotaAction(PersistenceService.IOTA_DELETE_FAIL,
                        "",
                        resp.getMsg());

                return resp;
            }
        }

        String filePath = propSource.getIriJarFilePath();

        if(AgentUtil.fileExists(filePath)) {
            try {
                File f = new File(filePath);
                rval = f.delete();
            } catch (Exception e) {
                msg = e.getLocalizedMessage();
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("deleteIotaFail") +
                        ": " + msg);
                rval = false;
            }
        }
        else {
            msg = filePath + " " + localizer.getLocalText("noFile");
            rval = true;
        }

        AgentUtil.deleteFileQuietly(propSource.getIotaAppDir() + "/iota.pid");

        AgentUtil.deleteFileQuietly(propSource.getIotaAppDir() + "/console.log");

        resp.setSuccess(rval);
        resp.setMsg(msg);
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, rval ? "true" : "false"));

        if(rval) {
            persister.logIotaAction(PersistenceService.IOTA_DELETE);
        }
        else {
            persister.logIotaAction(PersistenceService.IOTA_DELETE_FAIL);
        }
        return resp;
    }

}
