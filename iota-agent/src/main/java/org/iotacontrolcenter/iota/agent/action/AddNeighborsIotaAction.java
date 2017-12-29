package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.*;
import org.iotacontrolcenter.iota.agent.action.util.AgentUtil;
import org.iotacontrolcenter.iota.agent.http.AddIotaNeighbors;
import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.source.PropertySource;

public class AddNeighborsIotaAction extends AbstractAction implements IotaAction {

    public static final String ACTION_PROP = "addIotaNeighbors";

    public AddNeighborsIotaAction() {
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
            AddIotaNeighbors request = new AddIotaNeighbors(propSource.getLocalIotaUrl());

            IccrIotaNeighborsPropertyDto iotaNeighbors = propSource.getIotaNeighbors();

            if(iotaNeighbors == null || iotaNeighbors.getNeighbors() == null || iotaNeighbors.getNeighbors().isEmpty()) {
                System.out.println(ACTION_PROP + ", neighbors property is empty");
                resp.setSuccess(true);
                resp.setMsg("Neighbors was empty, nothing to add");
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                resp.setContent("Neighbors was empty, nothing to add");

                persistenceService.logIotaAction(PersistenceService.IOTA_ADD_NEIGHBORS_FAIL,
                        "",
                        "Neighbors configuration is empty");

                return resp;
            }

            IotaAddNeighborsCommandDto payload = new IotaAddNeighborsCommandDto();
            iotaNeighbors.getNeighbors().forEach((nbr) -> {
                if(nbr.isActive()) {
                    payload.addUri(nbr.getUri());
                }
            });

            if(payload.getUris().isEmpty()) {
                resp.setSuccess(true);
                resp.setMsg("No active neighbors, nothing to add");
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                resp.setContent("No active neighbors, nothing to add");

                persistenceService.logIotaAction(PersistenceService.IOTA_ADD_NEIGHBORS_FAIL,
                        "",
                        "No active neighbors, nothing to add");

                return resp;
            }

            request.setPayload(payload);

            try {
                request.execute();

                if(request.isResponseSuccess()) {

                    msg = "success";
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                    resp.setContent(request.responseAsString());

                    System.out.println(request.getName() + " " +
                            localization.getLocalText("httpRequestSuccess"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_ADD_NEIGHBORS);
                }
                else {
                    System.out.println(request.getName() + " addNeighbors response was not successful");

                    success = false;
                    msg = request.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = request.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                    persistenceService.logIotaAction(PersistenceService.IOTA_ADD_NEIGHBORS_FAIL,
                            "",
                            resp.getMsg());
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
