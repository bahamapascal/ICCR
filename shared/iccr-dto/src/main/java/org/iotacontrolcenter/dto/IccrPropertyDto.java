package org.iotacontrolcenter.dto;

public class IccrPropertyDto {
    private String id;

    public IccrPropertyDto() { }

    public IccrPropertyDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}