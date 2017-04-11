package org.iotacontrolcenter.iota.agent.action;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.GetIotaLibrary;
import org.iotacontrolcenter.iota.agent.process.IotaBackupAndInstallProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;

public class InstallIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "installIota";

    public InstallIotaAction() {
        super(new String[] { PropertySource.IOTA_DLD_LINK_PROP, PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    protected void validatePreconditions() {

        if (AgentUtil.dirDoesNotExist(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIotaAppDir());
        }

        if (AgentUtil.dirDoesNotExist(propSource.getIccrBakDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIccrBakDir());
        }

        if (AgentUtil.dirDoesNotExist(propSource.getIccrBakDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIccrBakDir());
        }

        if (AgentUtil.dirDoesNotExist(propSource.getIccrDownloadDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") + ": " + propSource.getIccrDownloadDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        System.out.println("installIota,  props: " + actionProps);
        preExecute();

        boolean wasIotaActive = AgentUtil.isIotaActive();

        ActionResponse resp = new ActionResponse();
        boolean success = true;
        String msg = "";

        // client may have provided the desired download link and filename:
        if(actionProps != null && actionProps.getProperties() != null && !actionProps.getProperties().isEmpty()) {
            for(IccrPropertyDto prop : actionProps.getProperties()) {
                System.out.println("installIota, using " + prop);
                propSource.setProperty(prop.getKey(), prop.getValue());
            }
        }

        System.out.println("installIota, from: " + propSource.getIotaDownloadUrl());

        // Download new IRI.jar:
        GetIotaLibrary iotaDld = new GetIotaLibrary(propSource.getIotaDownloadUrl());
        String dldFilePath;
        String iriJarFilePath;
        String iriJarFile;
        try {

            // First stop active in order to restart with new version just downloaded:
            if(wasIotaActive) {
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("stoppingIota"));

                boolean stopped = AgentUtil.stopIota();
                if(stopped) {
                    persistenceService.logIotaAction(PersistenceService.IOTA_STOP,
                            "",
                            "");
                }
                else {
                    System.out.println(ACTION_PROP + " " +
                            localization.getLocalText("stopIotaFail"));

                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localization.getLocalText("stopIotaFail"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_STOP_FAIL,
                            "",
                            resp.getMsg());

                    return resp;
                }
            }

            // Now do the download while it is stopping
            iotaDld.execute();

            if(iotaDld.isResponseSuccess()) {
                msg = "success";
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));

                System.out.println(iotaDld.getName() + " " +
                        localization.getLocalText("httpRequestSuccess"));

                String dldUrl = propSource.getIotaDownloadUrl();
                String dldFileName = dldUrl.substring(dldUrl.lastIndexOf("/")+1);

                // Store it in an ICCR download dir with timestamped suffix:
                dldFilePath = propSource.getIccrDownloadDir() + "/" + dldFileName + "." + propSource.getNowDateTimestamp();

                // And will be copied to the IRI jar file name specified in the iotaDir and iotaStartCmd props:
                iriJarFilePath = propSource.getIriJarFilePath();

                iriJarFile = propSource.getIriJarFileInStartCmd();

                byte[] jarBytes = iotaDld.responseAsByteArray();

                if(!storeJar(dldFilePath, jarBytes)) {
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localization.getLocalText("storeIotaFail"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_DLD_FAIL, dldFilePath,
                            localization.getLocalText("storeIotaFail"));

                    return resp;
                }

                persistenceService.logIotaAction(PersistenceService.IOTA_DLD,
                        propSource.getIotaDownloadUrl(),
                        dldFilePath + " (" + jarBytes.length + " bytes)");



                // Now copy into place the newly downloaded file, from download dir to the iota iri.jar file
                // that was specified in the start iota cmd:
                boolean installed = installNewIota(dldFilePath, iriJarFilePath, iriJarFile);
                if(installed) {
                    persistenceService.logIotaAction(PersistenceService.IOTA_INSTALL,
                            dldFilePath,
                            iriJarFilePath);
                }
                else {
                    System.out.println(ACTION_PROP + " " +
                            localization.getLocalText("installIotaFail"));

                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                    resp.setSuccess(false);
                    resp.setMsg(localization.getLocalText("installIotaFail"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_INSTALL_FAIL,
                            dldFilePath,
                            resp.getMsg());

                    return resp;
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
            else {
                success = false;
                msg = iotaDld.getResponseReason();
                if(msg == null || msg.isEmpty()) {
                    msg = iotaDld.getStartError();
                }
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                persistenceService.logIotaAction(PersistenceService.IOTA_DLD_FAIL, propSource.getIotaDownloadUrl(), msg);

                // Adding url to msg to send back to client after the action was logged
                msg = propSource.getIotaDownloadUrl() + " " + msg;
            }
        }
        catch(IllegalStateException ise) {

            persistenceService.logIotaAction(PersistenceService.IOTA_DLD_FAIL, propSource.getIotaDownloadUrl(), ise.getMessage());

            // Message is already localized
            System.out.println(iotaDld.getName() + " " +
                    localization.getLocalTextWithFixed("startHttpException", ise.getMessage()));
            success = false;
            msg = ise.getMessage();
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resp.setSuccess(success);
        resp.setMsg(msg);

        return resp;
    }

    private boolean installNewIota(String dldFilePath, String iriJarFilePath, String iriJarFile) {

        System.out.println("installing new iri jar from " + dldFilePath +
                ", copying to " + iriJarFilePath);

        // General preconditions were already validated (i.e. iotaDir exists)
        if(!AgentUtil.fileExists(dldFilePath)) {
            System.out.println(ACTION_PROP + " " +
                    localization.getLocalText("storeIotaFail") +
                    ": (" + dldFilePath + "), " +
                    localization.getLocalText("missingFile"));
            return false;
        }

        if(AgentUtil.fileExists(iriJarFilePath)) {
            String iriBackupFilePath = propSource.getIccrBakDir() + "/" +
                    iriJarFile + "." + propSource.getNowDateTimestamp();

            System.out.println("installNewIota make backup: " +
                    "copy " + iriJarFilePath + " to " + iriBackupFilePath);

            try {
                FileUtils.copyFile(new File(iriJarFilePath), new File(iriBackupFilePath));
            }
            catch(IOException ioe) {
                System.out.println(ACTION_PROP + " " +
                        localization.getLocalText("backupIotaFail") +
                        ": (" + iriJarFilePath + ")");
                return false;
            }
        }

        System.out.println("installNewIota installing: " +
                "copy " + dldFilePath + " to " + iriJarFilePath);

        try {
            FileUtils.copyFile(new File(dldFilePath), new File(iriJarFilePath));
        }
        catch(IOException ioe) {
            System.out.println(ACTION_PROP + " " +
                    localization.getLocalText("installIotaFail") +
                    ": (" + dldFilePath + " -> " + iriJarFilePath + ")");
            return false;
        }
        return true;
    }

    private boolean installNewIotaByScript(String dldFilePath, String iriJarFile) {
        boolean success;

        System.out.println("installing new iri jar from " + dldFilePath +
                ", copying to " + iriJarFile);

        // run script to: install-new-iri <dldFilepath> <iriJarFile>
        //      make existing iri backup
        //      mv dld to iri
        OsProcess proc = new IotaBackupAndInstallProcess(dldFilePath, iriJarFile);
        success = proc.start();
        String msg = localization.getLocalText("processSuccess");
        int rc = 0;
        if (!success) {
            if (proc.isStartError()) {
                System.out.println(proc.getStartError());
            }
        } else {
            rc = proc.getResultCode();
            System.out.println(proc.getName() + " " +
                    localization.getLocalText("processSuccess") + ", " +
                    localization.getLocalText("resultCode") + ": " + rc);
        }

        /*
        resp.setSuccess(success);
        resp.setMsg(msg);

        if(success) {
            resp.addProperty(new IccrPropertyDto("resultCode", Integer.toString(rc)));
        }
        resp.addProperty(new IccrPropertyDto(ACTION_PROP, (rc == 0 ? "true" : "false")));
        */
        success = success && rc == 0;

        return success;
    }

    private boolean storeJar(String filePath, byte[] jarBytes) {
        boolean success = true;

        System.out.println("storing downloaded iri jar bytes in " + filePath);

        try {
            FileUtils.writeByteArrayToFile(new File(filePath), jarBytes);
        }
        catch(IOException ioe) {
            System.out.println(ACTION_PROP + " " +
                    localization.getLocalText("storeIotaFail") +
                    ": " + ioe.getLocalizedMessage());
            success = false;
        }
        return success;
    }

}
