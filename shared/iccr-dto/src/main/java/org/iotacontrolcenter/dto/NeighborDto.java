package org.iotacontrolcenter.dto;


public class NeighborDto {
    private String key;
    private String ip;
    private String name;
    private String descr;
    private boolean active;

    public NeighborDto() {
    }

    public NeighborDto(String key, String ip, String name, String descr, boolean active) {
        this.key = key;
        this.ip = ip;
        this.name = name;
        this.descr = descr;
        this.active = active;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "NeighborDto{" +
                "key='" + key + '\'' +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", active=" + active +
                '}';
    }
}
