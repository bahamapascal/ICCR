package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;

public interface IotaAction {
    ActionResponse execute();
    boolean setup();
}
