package org.iotacontrolcenter.dto;

import java.util.ArrayList;
import java.util.List;

public class IccrPropertyListDto {
    private List<IccrPropertyDto> properties;

    public List<IccrPropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<IccrPropertyDto> properties) {
        this.properties = properties;
    }

    public void addProperty(IccrPropertyDto property) {
        if(properties == null) {
            properties = new ArrayList<>();
        }
        properties.add(property);
    }

    @Override
    public String toString() {
        return "IccrPropertyListDto{" +
                "properties=" + properties +
                '}';
    }
}
