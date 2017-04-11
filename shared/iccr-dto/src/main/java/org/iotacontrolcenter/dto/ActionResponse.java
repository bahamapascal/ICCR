package org.iotacontrolcenter.dto;

import java.util.ArrayList;
import java.util.List;

public class ActionResponse {
    public boolean success;
    public String msg;
    public String content;
    private List<IccrPropertyDto> properties;

    public ActionResponse() {
    }

    public ActionResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public IccrPropertyDto getProperty(String key) {
        IccrPropertyDto propertyDto = null;
        if(properties != null) {
            for (IccrPropertyDto dto : properties) {
                if (dto.getKey().equals(key)) {
                    propertyDto = dto;
                    break;
                }
            }
        }
        return propertyDto;
    }
}
