package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
import org.iotacontrolcenter.properties.source.PropertySource;

public class StartIotaAction extends AbstractAction implements IotaAction {

    public StartIotaAction() {
        super(new String[] { PropertySource.IOTA_APP_DIR_PROP,
                        PropertySource.IOTA_PORT_NUMBER_PROP,
                        PropertySource.IOTA_START_PROP});
    }

    @Override
    public ActionResponse execute() {
        preExecute();
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }

    @Override
    protected void validatePreconditions() {
        IccrIotaNeighborsPropertyDto nbrs = propSource.getIotaNeighbors();
        if(nbrs == null || nbrs.getNbrs().isEmpty()) {
            throw new IllegalStateException(localizer.getLocalText("missingProperty") + ": " + PropertySource.IOTA_NEIGHBORS_PROP);
        }
    }
}
