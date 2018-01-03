package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyListDto;

public interface IotaAction {
    ActionResponse execute(IccrPropertyListDto actionProps);
}
