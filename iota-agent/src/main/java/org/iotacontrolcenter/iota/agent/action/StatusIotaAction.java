package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.iota.agent.http.GetIotaNodeInfo;
import org.iotacontrolcenter.iota.agent.http.HttpGet;
import org.iotacontrolcenter.iota.agent.process.IotaStatusProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StatusIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "statusIota";

    public StatusIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP,
                                PropertySource.IOTA_PORT_NUMBER_PROP});
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();
        boolean rval = true;
        String msg = null;

        if(propSource.osIsWindows()) {
            GetIotaNodeInfo nodeInfoReq = new GetIotaNodeInfo(propSource.getLocalIotaUrl());

            try {
                nodeInfoReq.execute();

                if(nodeInfoReq.isResponseSuccess()) {
                    msg = "success";
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                    resp.addProperty(new IccrPropertyDto("content", nodeInfoReq.responseAsString()));

                    System.out.println(nodeInfoReq.getName() + " " +
                            localizer.getLocalText("httpRequestSuccess"));
                }
                else {
                    rval = false;
                    msg = nodeInfoReq.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = nodeInfoReq.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                }
            }
            catch(IllegalStateException ise) {
                // Message is already localized
                System.out.println(nodeInfoReq.getName() + " " +
                        localizer.getLocalTextWithFixed("startHttpException", ise.getMessage()));
                rval = false;
                msg = ise.getMessage();
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            }

            resp.setSuccess(rval);
            resp.setMsg(msg);
        }
        else {
            OsProcess proc = new IotaStatusProcess();
            rval = proc.start();
            msg = localizer.getLocalText("processSuccess");
            int rc = 0;
            if (!rval) {
                if (proc.isStartError()) {
                    System.out.println(proc.getStartError());
                    msg = proc.getStartError();
                }
                else {
                    msg = localizer.getLocalText("processFail");
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
        }
        return resp;
    }
}
