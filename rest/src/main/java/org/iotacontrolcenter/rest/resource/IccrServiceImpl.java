package org.iotacontrolcenter.rest.resource;

import java.util.ArrayList;
import java.util.List;
import org.iotacontrolcenter.api.*;
import org.iotacontrolcenter.dto.*;

public class IccrServiceImpl implements IccrService {

    public IccrServiceImpl() {
    }

    @Override
    public List<IccrPropertyDto> getConfigPropertyList() {
        List<IccrPropertyDto> props = new ArrayList<>();
        props.add(new IccrPropertyDto(("bill")));
        return props;
    }

    @Override
    public IccrPropertyDto getConfigProperty(String id) {
        IccrPropertyDto prop = new IccrPropertyDto();
        prop.setId("fred");
        return prop;
    }
}