package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;

public class DeleteIotaAction extends AbstractAction implements IotaAction  {

    public static final String ACTION_PROP = "deleteIota";

    public DeleteIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    protected void validatePreconditions() {
        if (AgentUtil.dirDoesNotExist(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) throws InterruptedException {
        preExecute();

        ActionResponse resp = new ActionResponse();

        boolean success;
        String msg = "";

        if (AgentUtil.isIotaActive()) {
            System.out.println("deleteIota, first stopping");
            if(!AgentUtil.stopIota()) {
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("deleteIotaFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localization.getLocalText("deleteIotaFail"));

                persistenceService.logIotaAction(PersistenceService.IOTA_DELETE_FAIL,
                        "",
                        resp.getMsg());

                return resp;
            }
        }

        String filePath = propSource.getIriJarFilePath();

        if(AgentUtil.fileExists(filePath)) {
            try {
                File f = new File(filePath);
                success = f.delete();
            } catch (Exception e) {
                msg = e.getLocalizedMessage();
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("deleteIotaFail") +
                        ": " + msg);
                success = false;
            }
        }
        else {
            msg = filePath + " " + localization.getLocalText("noFile");
            success = true;
        }

        AgentUtil.deleteFileQuietly(propSource.getIotaAppDir() + "/iota.pid");

        AgentUtil.deleteFileQuietly(propSource.getIotaAppDir() + "/console.log");

        resp.setSuccess(success);
        resp.setMsg(msg);
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, success ? "true" : "false"));

        if(success) {
            persistenceService.logIotaAction(PersistenceService.IOTA_DELETE);
        }
        else {
            persistenceService.logIotaAction(PersistenceService.IOTA_DELETE_FAIL);
        }
        return resp;
    }

}
