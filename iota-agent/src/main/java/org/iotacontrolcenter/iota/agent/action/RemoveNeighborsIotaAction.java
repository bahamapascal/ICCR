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
            RemoveIotaNeighbors request = new RemoveIotaNeighbors(propSource.getLocalIotaUrl());

            IotaRemoveNeighborsCommandDto payload = new IotaRemoveNeighborsCommandDto();

            if(actionProps != null && actionProps.getProperties() != null &&
                    !actionProps.getProperties().isEmpty()) {
                // Removing only the specified nbrs:
                for(IccrPropertyDto nbr : actionProps.getProperties()) {
                    payload.addUri(nbr.getValue());
                }
            }
            else {
                // Removing all the currently configured nbrs:
                IccrIotaNeighborsPropertyDto nbrs = propSource.getIotaNeighbors();
                nbrs.getNbrs().forEach((nbr) -> {
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
                            localizer.getLocalText("httpRequestSuccess"));

                    persister.logIotaAction(PersistenceService.IOTA_REMOVE_NBRS);
                }
                else {
                    rval = false;
                    msg = request.getResponseReason();
                    if(msg == null || msg.isEmpty()) {
                        msg = request.getStartError();
                    }
                    resp.addProperty(new IccrPropertyDto(ACTION_PROP, "false"));

                    persister.logIotaAction(PersistenceService.IOTA_REMOVE_NBRS_FAIL,
                            "",
                            resp.getMsg());
                }
            }
            catch(IllegalStateException ise) {
                // Message is already localized
                System.out.println(request.getName() + " " +
                        localizer.getLocalTextWithFixed("startHttpException", ise.getMessage()));
                ise.printStackTrace();

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
