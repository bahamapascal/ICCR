package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.properties.source.PropertySource;

public class InstallIotaAction extends AbstractAction implements IotaAction {


    public InstallIotaAction() {
        super(new String[] { PropertySource.IOTA_DLD_LINK_PROP, PropertySource.IOTA_DLD_FILENAME_PROP,
                PropertySource.IOTA_APP_DIR_PROP });
    }

    @Override
    public ActionResponse execute() {
        preExecute();
        ActionResponse resp = new ActionResponse(true, "happy");
        return resp;
    }

}
