package org.iotacontrolcenter.iota.agent.action;


import org.iotacontrolcenter.dto.*;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.RemoveIotaNeighbors;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

public class RemoveNeighborsIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "removeIotaNeighbors";

    public RemoveNeighborsIotaAction() {
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
            RemoveIotaNeighbors request = new RemoveIotaNeighbors(propSource.getLocalIotaUrl());

            IotaRemoveNeighborsCommandDto payload = new IotaRemoveNeighborsCommandDto();

            if(actionProps != null && actionProps.getProperties() != null &&
                    !actionProps.getProperties().isEmpty()) {
                // Removing only the specified neighbors:
                for(IccrPropertyDto nbr : actionProps.getProperties()) {
                    payload.addUri(nbr.getValue());
                }
            }
            else {
                // Removing all the currently configured neighbors:
                IccrIotaNeighborsPropertyDto iotaNeighborsPropertyDto = propSource.getIotaNeighbors();
                iotaNeighborsPropertyDto.getNeighbors().forEach((nbr) -> {
                    if (nbr.isActive()) {
                        payload.addUri(nbr.getUri());
                    }
                });
            }

            System.out.println(ACTION_PROP + ", " + payload);

            request.setPayload(payload);

            try {
                request.execute();

                if(request.isResponseSuccess()) {
                    msg = "success";
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                    resp.setContent(request.responseAsString());

                    System.out.println(request.getName() + " " +
                            localization.getLocalText("httpRequestSuccess"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_REMOVE_NEIGHBORS);
                }
                else {
                    success = false;
                    msg = request.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = request.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_REMOVE_NEIGHBORS_FAIL,
                            "",
                            resp.getMsg());
                }
            }
            catch(IllegalStateException ise) {
                // Message is already localized
                System.out.println(request.getName() + " " +
                        localization.getLocalTextWithFixed("startHttpException", ise.getMessage()));
                ise.printStackTrace();

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
