package org.iotacontrolcenter.iota.agent.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;


public class DeleteDbIotaAction extends AbstractAction implements IotaAction  {

    public static final String ACTION_PROP = "deleteIotaDb";

    public DeleteDbIotaAction() {
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

        boolean success = true;
        String msg = "";

        boolean wasIotaActive = AgentUtil.isIotaActive();

        if (wasIotaActive) {
            System.out.println(ACTION_PROP + ", first stopping IOTA");
            if(!AgentUtil.stopIota()) {
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("deleteIotaDbFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localization.getLocalText("deleteIotaDbFail") + " failed to stop IOTA");

                persistenceService.logIotaAction(PersistenceService.IOTA_DELETE_DB_FAIL,
                        "",
                        resp.getMsg());

                return resp;
            }
        }

        try {
            Path dir = Paths.get(propSource.getIotaAppDir());
            Files.newDirectoryStream(dir).forEach(file -> {
                try {
                    if (Files.isDirectory(file) && (file.endsWith("mainnetdb")
                            || file.endsWith("testnetdb"))) {
                        
                        FileUtils.deleteDirectory(file.toFile());
                    }
                }
                catch (IOException ioe) {
                    //msg = ioe.getLocalizedMessage();
                    System.out.println(ACTION_PROP + " " +
                            localization.getLocalText("deleteIotaDbFail") +
                            ": " + ioe.getLocalizedMessage());

                    ioe.printStackTrace();

                    //success = false;
                }
            } );
        }
        catch (IOException ioe) {
            msg = ioe.getLocalizedMessage();
            System.out.println(ACTION_PROP + " " +
                    localization.getLocalText("deleteIotaDbFail") +
                    ": " + msg);

            ioe.printStackTrace();

            success = false;
        }

        if(success) {
            persistenceService.logIotaAction(PersistenceService.IOTA_DELETE_DB);
        }
        else {
            persistenceService.logIotaAction(PersistenceService.IOTA_DELETE_DB_FAIL);
        }

        if(wasIotaActive) {
            System.out.println(ACTION_PROP + ", restarting IOTA");

            if(success) {

                // Pause for a sec to let it spin up
                Thread.sleep(2000);

            }

            boolean started = AgentUtil.startIotaBoolean();
            //noinspection StatementWithEmptyBody
            if(started) {
                // The start action is logging this event
            }
            else {
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("startIotaFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localization.getLocalText("startIotaFail"));

                return resp;
            }
        }

        resp.setSuccess(success);
        resp.setMsg(msg);
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, success ? "true" : "false"));

        return resp;
    }
}
