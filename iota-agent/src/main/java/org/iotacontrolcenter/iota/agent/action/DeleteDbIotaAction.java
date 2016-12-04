package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeleteDbIotaAction extends AbstractAction implements IotaAction  {

    public static final String ACTION_PROP = "deleteIotaDb";
    private boolean wasIotaActive = false;

    public DeleteDbIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    protected void validatePreconditions() {
        if (!AgentUtil.dirExists(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();

        boolean rval = true;
        String msg = "";

        wasIotaActive = AgentUtil.isIotaActive();

        if (wasIotaActive) {
            System.out.println(ACTION_PROP + ", first stopping IOTA");
            if(!AgentUtil.stopIota()) {
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("deleteIotaDbFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localizer.getLocalText("deleteIotaDbFail") + " failed to stop IOTA");

                persister.logIotaAction(PersistenceService.IOTA_DELETE_DB_FAIL,
                        "",
                        resp.getMsg());

                return resp;
            }
        }

        try {
            Path dir = Paths.get(propSource.getIotaAppDir());
            Files.newDirectoryStream(dir).forEach(file -> {
                try {
                    if(file.getFileName().toString().endsWith(".iri")) {
                        Files.delete(file);
                    }
                }
                catch (IOException ioe) {
                    //msg = ioe.getLocalizedMessage();
                    System.out.println(ACTION_PROP + " " +
                            localizer.getLocalText("deleteIotaDbFail") +
                            ": " + ioe.getLocalizedMessage());

                    ioe.printStackTrace();

                    //rval = false;
                }
            } );
        }
        catch (IOException ioe) {
            msg = ioe.getLocalizedMessage();
            System.out.println(ACTION_PROP + " " +
                    localizer.getLocalText("deleteIotaDbFail") +
                    ": " + msg);

            ioe.printStackTrace();

            rval = false;
        }

        if(rval) {
            persister.logIotaAction(PersistenceService.IOTA_DELETE_DB);
        }
        else {
            persister.logIotaAction(PersistenceService.IOTA_DELETE_DB_FAIL);
        }

        if(wasIotaActive) {
            System.out.println(ACTION_PROP + ", restarting IOTA");

            if(rval) {

                // Pause for a sec to let it spin up
                try {
                    Thread.sleep(2000);
                }
                catch(Exception e) {
                }

            }

            boolean started = AgentUtil.startIotaBoolean();
            if(started) {
                // The start action is logging this event
            }
            else {
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("startIotaFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localizer.getLocalText("startIotaFail"));

                return resp;
            }
        }

        resp.setSuccess(rval);
        resp.setMsg(msg);
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, rval ? "true" : "false"));

        return resp;
    }
}
