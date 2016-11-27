package org.iotacontrolcenter.dto;


import java.util.ArrayList;
import java.util.List;

public class IotaNeighborsCommandDto extends IotaCommandDto {

    public static final String CMD = "addNeighbors";

    private List<String> uris;

    public IotaNeighborsCommandDto() {
        super(CMD);
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public void addUri(String uri) {
        if(uris == null) {
            uris = new ArrayList<>();
        }
        uris.add(uri);
    }
}
