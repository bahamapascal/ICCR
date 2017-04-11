package org.iotacontrolcenter.iota.agent.action;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.dto.IotaGetNodeInfoResponseDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.GetIotaNodeInfo;
import org.iotacontrolcenter.properties.source.PropertySource;

public class NodeInfoIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "getIotaNodeInfo";

    public NodeInfoIotaAction() {
        super(new String[]{PropertySource.IOTA_APP_DIR_PROP});
    }

    @Override
    protected void validatePreconditions() {

        if (AgentUtil.dirDoesNotExist(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localization.getLocalText("missingDirectory") +
                    ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();
        String msg;
        boolean success = true;

        if (!AgentUtil.isIotaActive()) {
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            resp.setSuccess(false);
            resp.setMsg(localization.getLocalText("iotaNotActive"));
        }
        else {
            GetIotaNodeInfo request = new GetIotaNodeInfo(propSource.getLocalIotaUrl());

            try {
                request.execute();

                if(request.isResponseSuccess()) {
                    msg = "success";
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                    resp.setContent(request.responseAsString());

                    IotaGetNodeInfoResponseDto dto;
                    try {
                        Gson gson = new GsonBuilder().create();
                        dto = gson.fromJson(resp.getContent(), IotaGetNodeInfoResponseDto.class);

                        System.out.println("mapped " + ACTION_PROP + " response dto: " + dto);
                    }
                    catch(Exception e) {
                        System.out.println(ACTION_PROP + ", exception mapping json response: " + e);
                    }

                    System.out.println(ACTION_PROP + ", content: " + resp.getContent());

                    //request.rep

                    System.out.println(request.getName() + " " +
                            localization.getLocalText("httpRequestSuccess"));
                }
                else {
                    success = false;
                    msg = request.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = request.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
                }
            }
            catch(IllegalStateException ise) {
                // Message is already localized
                System.out.println(request.getName() + " " +
                        localization.getLocalTextWithFixed("startHttpException", ise.getMessage()));
                success = false;
                msg = ise.getMessage();
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            }

            resp.setSuccess(success);
            resp.setMsg(msg);
        }

        return resp;
    }
}
