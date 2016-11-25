package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.http.GetIotaLibrary;
import org.iotacontrolcenter.properties.source.PropertySource;

public class InstallIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "installIota";
    private boolean wasIotaActive = false;

    public InstallIotaAction() {
        super(new String[] { PropertySource.IOTA_DLD_LINK_PROP, PropertySource.IOTA_DLD_FILENAME_PROP,
                PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    public ActionResponse execute() {
        preExecute();

        setWasIotaActive();

        ActionResponse resp = new ActionResponse();
        boolean rval = true;
        String msg = null;

        GetIotaLibrary iotaDld = new GetIotaLibrary(propSource.getIotaDownloadUrl());

        try {
            iotaDld.execute();

            System.out.println("execute done, checking resp success");

            if(iotaDld.isResponseSuccess()) {
                msg = "success";
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));

                System.out.println(iotaDld.getName() + " " +
                        localizer.getLocalText("httpRequestSuccess"));

                System.out.println(iotaDld.getName() + ", getting byte array");
                byte[] jarBytes = iotaDld.responseAsByteArray();

                System.out.println("Downloaded " + jarBytes.length  + "  sized jar");

                if(wasIotaActive) {
                    System.out.println("IOTA was active, stopping it...");
                    boolean stopped = stopIota();
                    if(!stopped) {
                        System.out.println("Failed to stop IOTA");
                    }
                }
                else {
                    System.out.println("IOTA was not active");
                }

                // Make backup
                // Write new jar
                // Start iota
                // Add neighbors
            }
            else {
                rval = false;
                msg = iotaDld.getResponseReason();
                if(msg == null || msg.isEmpty()) {
                    msg = iotaDld.getStartError();
                }
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            }
        }
        catch(IllegalStateException ise) {
            // Message is already localized
            System.out.println(iotaDld.getName() + " " +
                    localizer.getLocalTextWithFixed("startHttpException", ise.getMessage()));
            rval = false;
            msg = ise.getMessage();
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
        }

        resp.setSuccess(rval);
        resp.setMsg(msg);

        return resp;
    }

    private void setWasIotaActive() {
        StatusIotaAction status = new StatusIotaAction();
        ActionResponse resp = status.execute();
        wasIotaActive = resp.isSuccess() &&
                resp.getProperty(StatusIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StatusIotaAction.ACTION_PROP).getValue().equals("true");
    }

    private boolean stopIota() {
        StopIotaAction stopper = new StopIotaAction();
        ActionResponse resp = stopper.execute();
        return resp.isSuccess() &&
                resp.getProperty(StopIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StopIotaAction.ACTION_PROP).getValue().equals("true");
    }

}
