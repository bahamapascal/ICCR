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
            AddIotaNeighbors request = new AddIotaNeighbors(propSource.getLocalIotaUrl());

            IccrIotaNeighborsPropertyDto nbrs = propSource.getIotaNeighbors();

            if(nbrs == null || nbrs.getNbrs() == null || nbrs.getNbrs().isEmpty()) {
                System.out.println(ACTION_PROP + ", neighbors property is empty");
                resp.setSuccess(true);
                resp.setMsg("Neighbors was empty, nothing to add");
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                resp.setContent("Neighbors was empty, nothing to add");

                persister.logIotaAction(PersistenceService.IOTA_ADD_NBRS_FAIL,
                        "",
                        "Neighbors configuration is empty");

                return resp;
            }

            IotaAddNeighborsCommandDto payload = new IotaAddNeighborsCommandDto();
            nbrs.getNbrs().forEach((nbr) -> {
                if(nbr.isActive()) {
                    payload.addUri(nbr.getUri());
                }
            });

            if(payload.getUris().isEmpty()) {
                resp.setSuccess(true);
                resp.setMsg("No active neighbors, nothing to add");
                resp.addProperty(new IccrPropertyDto(ACTION_PROP, "true"));
                resp.setContent("No active neighbors, nothing to add");

                persister.logIotaAction(PersistenceService.IOTA_ADD_NBRS_FAIL,
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
                            localizer.getLocalText("httpRequestSuccess"));

                    persister.logIotaAction(PersistenceService.IOTA_ADD_NBRS);
                }
                else {
                    System.out.println(request.getName() + " addNeighbors response was not successful");

                    rval = false;
                    msg = request.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = request.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                    persister.logIotaAction(PersistenceService.IOTA_ADD_NBRS_FAIL,
                            "",
                            resp.getMsg());
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
