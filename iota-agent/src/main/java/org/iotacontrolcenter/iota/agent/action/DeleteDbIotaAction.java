package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
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
    public ActionResponse execute() {
        preExecute();

        ActionResponse resp = new ActionResponse();

        boolean rval = true;
        String msg = "";

        if (AgentUtil.isIotaActive()) {
            System.out.println("deleteIotaDb, first stopping");
            if(!AgentUtil.stopIota()) {
                System.out.println(ACTION_PROP + " " +
                        localizer.getLocalText("deleteIotaDbFail"));

                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                resp.setSuccess(false);
                resp.setMsg(localizer.getLocalText("deleteIotaDbFail"));

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
                    //rval = false;
                }
            } );
        }
        catch (IOException ioe) {
            msg = ioe.getLocalizedMessage();
            System.out.println(ACTION_PROP + " " +
                    localizer.getLocalText("deleteIotaDbFail") +
                    ": " + msg);
            rval = false;
        }

        resp.setSuccess(rval);
        resp.setMsg(msg);
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, rval ? "true" : "false"));

        if(rval) {
            persister.logIotaAction(PersistenceService.IOTA_DELETE_DB);
        }
        else {
            persister.logIotaAction(PersistenceService.IOTA_DELETE_DB_FAIL);
        }
        return resp;
    }
}
