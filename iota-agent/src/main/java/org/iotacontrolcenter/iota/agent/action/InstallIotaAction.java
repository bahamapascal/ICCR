package org.iotacontrolcenter.iota.agent.action;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.GetIotaLibrary;
import org.iotacontrolcenter.iota.agent.process.IotaBakupAndInstallProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;

public class InstallIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "installIota";
    private boolean wasIotaActive = false;

    public InstallIotaAction() {
        super(new String[] { PropertySource.IOTA_DLD_LINK_PROP, PropertySource.IOTA_DLD_FILENAME_PROP,
                PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    protected void validatePreconditions() {

        if (!AgentUtil.dirExists(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }

        if (!AgentUtil.dirExists(propSource.getIccrBakDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIccrBakDir());
        }

        if (!AgentUtil.dirExists(propSource.getIccrBakDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIccrBakDir());
        }

        if (!AgentUtil.dirExists(propSource.getIccrDownloadDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") + ": " + propSource.getIccrDownloadDir());
        }
    }

    @Override
    public ActionResponse execute() {
        preExecute();

        setWasIotaActive();

        ActionResponse resp = new ActionResponse();
        boolean rval = true;
        String msg = null;

        // Download new IRI.jar:
        GetIotaLibrary iotaDld = new GetIotaLibrary(propSource.getIotaDownloadUrl());
        String dldFilePath = null;
        String iriJarFilePath = null;
        String iriJarFile= null;
        try {
            iotaDld.execute();

            if(iotaDld.isResponseSuccess()) {
                msg = "success";
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));

                System.out.println(iotaDld.getName() + " " +
                        localizer.getLocalText("httpRequestSuccess"));

                // Store it in an ICCR download dir with timestamped suffix:
                dldFilePath = propSource.getIccrDownloadDir() + "/" +
                        propSource.getIotaDownloadFilename() + "." + propSource.getNowDateTimestamp();

                // And will be copied to the IRI jar file name specified in the iotaDir and iotaStartCmd props:
                iriJarFilePath = propSource.getIriJarFilePath();

                iriJarFile = propSource.getIriJarFileInStartCmd();

                byte[] jarBytes = iotaDld.responseAsByteArray();

                if(!storeJar(dldFilePath, jarBytes)) {
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localizer.getLocalText("storeIotaFail"));

                    persister.logIotaAction(PersistenceService.IOTA_DLD_FAIL, dldFilePath,
                            localizer.getLocalText("storeIotaFail"));

                    return resp;
                }

                persister.logIotaAction(PersistenceService.IOTA_DLD,
                        propSource.getIotaDownloadUrl(),
                        dldFilePath + " (" + jarBytes.length + " bytes)");

                // Stop active in order to restart with new version just downloaded:
                if(wasIotaActive) {
                    System.out.println(ACTION_PROP + " " +
                            localizer.getLocalText("stoppingIota"));

                    boolean stopped = stopIota();
                    if(stopped) {
                        persister.logIotaAction(PersistenceService.IOTA_STOP,
                                "",
                                "");
                    }
                    else {
                        System.out.println(ACTION_PROP + " " +
                                localizer.getLocalText("stopIotaFail"));

                        resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                        resp.setSuccess(false);
                        resp.setMsg(localizer.getLocalText("stopIotaFail"));

                        persister.logIotaAction(PersistenceService.IOTA_STOP_FAIL,
                                "",
                                resp.getMsg());

                        return resp;
                    }
                }

                // Now copy into place the newly downloaded file, from download dir to the iota iri.jar file
                // that was specified in the start iota cmd:
                boolean installed = installNewIota(dldFilePath, iriJarFile);
                if(installed) {
                    persister.logIotaAction(PersistenceService.IOTA_INSTALL,
                            dldFilePath,
                            iriJarFilePath);
                }
                else {
                    System.out.println(ACTION_PROP + " " +
                            localizer.getLocalText("installIotaFail"));

                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localizer.getLocalText("installIotaFail"));

                    persister.logIotaAction(PersistenceService.IOTA_INSTALL_FAIL,
                            dldFilePath,
                            resp.getMsg());

                    return resp;
                }

                boolean started = startIota();
                if(started) {
                    persister.logIotaAction(PersistenceService.IOTA_START,
                            propSource.getIotaStartCmd(),
                            "");
                }
                else {
                    System.out.println(ACTION_PROP + " " +
                            localizer.getLocalText("startIotaFail"));

                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localizer.getLocalText("startIotaFail"));

                    persister.logIotaAction(PersistenceService.IOTA_START_FAIL,
                            propSource.getIotaStartCmd(),
                            resp.getMsg());

                    return resp;
                }

                // Add neighbors is done in start?
            }
            else {
                rval = false;
                msg = iotaDld.getResponseReason();
                if(msg == null || msg.isEmpty()) {
                    msg = iotaDld.getStartError();
                }
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                persister.logIotaAction(PersistenceService.IOTA_DLD_FAIL, propSource.getIotaDownloadUrl(), msg);
            }
        }
        catch(IllegalStateException ise) {

            persister.logIotaAction(PersistenceService.IOTA_DLD_FAIL, propSource.getIotaDownloadUrl(), ise.getMessage());

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

    private boolean installNewIota(String dldFilePath, String iriJarFile) {
        boolean rval = true;

        System.out.println("installing new iri jar from " + dldFilePath +
                ", copying to " + iriJarFile);

        // run script to: install-new-iri <dldFilepath> <iriJarFile>
        //      make existing iri backup
        //      mv dld to iri
        OsProcess proc = new IotaBakupAndInstallProcess(dldFilePath, iriJarFile);
        rval = proc.start();
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

        /*
        resp.setSuccess(rval);
        resp.setMsg(msg);

        if(rval) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));
        */
        rval = rval && rc == 0;

        return rval;
    }

    private boolean storeJar(String filePath, byte[] jarBytes) {
        boolean rval = true;

        System.out.println("storing downloaded iri jar bytes in " + filePath);

        try {
            FileUtils.writeByteArrayToFile(new File(filePath), jarBytes);
        }
        catch(IOException ioe) {
            System.out.println(ACTION_PROP + " " +
                    localizer.getLocalText("storeIotaFail") +
                    ": " + ioe.getLocalizedMessage());
            rval = false;
        }
        return rval;
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

    private boolean startIota() {
        StartIotaAction starter = new StartIotaAction();
        ActionResponse resp = starter.execute();
        return resp.isSuccess() &&
                resp.getProperty(StartIotaAction.ACTION_PROP) != null &&
                resp.getProperty(StartIotaAction.ACTION_PROP).getValue().equals("true");
    }

}
