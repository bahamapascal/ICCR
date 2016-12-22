package org.iotacontrolcenter.iccr.agent.action;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrPropertyListDto;

public interface IccrAction {
    ActionResponse execute(IccrPropertyListDto actionProps);
}
