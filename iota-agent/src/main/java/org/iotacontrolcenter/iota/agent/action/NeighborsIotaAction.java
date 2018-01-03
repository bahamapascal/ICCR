package org.iotacontrolcenter.iota.agent.action;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;
import org.iotacontrolcenter.dto.IotaGetNeighborsResponseDto;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.GetIotaNeighbors;
import org.iotacontrolcenter.properties.source.PropertySource;

public class NeighborsIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "getIotaNeighbors";

    public NeighborsIotaAction() {
        super(new String[]{PropertySource.IOTA_APP_DIR_PROP});
    }

    @Override
    protected void validatePreconditions() {

        if (!AgentUtil.dirExists(propSource.getIotaAppDir())) {
            throw new IllegalStateException(localizer.getLocalText("missingDirectory") +
                    ": " + propSource.getIotaAppDir());
        }
    }

    @Override
    public ActionResponse execute(IccrPropertyListDto actionProps) {
        preExecute();

        ActionResponse resp = new ActionResponse();
        String msg = "";
        boolean rval = true;

        if (!AgentUtil.isIotaActive()) {
            resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            resp.setSuccess(false);
            resp.setMsg(localizer.getLocalText("iotaNotActive"));
        }
        else {
            GetIotaNeighbors request = new GetIotaNeighbors(propSource.getLocalIotaUrl());

            try {
                request.execute();

                if(request.isResponseSuccess()) {
                    msg = "success";
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                    resp.setContent(request.responseAsString());

                    IotaGetNeighborsResponseDto dto = null;

                    try {
                        Gson gson = new GsonBuilder().create();
                        dto = gson.fromJson(resp.getContent(), IotaGetNeighborsResponseDto.class);

                        System.out.println("mapped " + ACTION_PROP + " response dto: " + dto);
                    }
                    catch(Exception e) {
                        System.out.println(ACTION_PROP + ", exception mapping json response: " + e);
                    }

                    System.out.println(ACTION_PROP + ", content: " + resp.getContent());

                    System.out.println(request.getName() + " " +
                            localizer.getLocalText("httpRequestSuccess"));
                }
                else {
                    rval = false;
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
                        localizer.getLocalTextWithFixed("startHttpException", ise.getMessage()));
                rval = false;
                msg = ise.getMessage();
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));
            }

            resp.setSuccess(rval);
            resp.setMsg(msg);
        }

        return resp;
    }
}
