package org.iotacontrolcenter.dto;


public class NeighborDto {

    private boolean active;
    private String descr;
    private String ip;
    private String key;
    private String name;
    private int port = 14265;
    private String scheme = "udp";

    public NeighborDto() {
    }

    public NeighborDto(String key, String ip, String name, String descr,
                       boolean active, int port, String scheme) {
        this.key = key;
        this.ip = ip;
        this.name = name;
        this.descr = descr;
        this.active = active;
        this.port = port;
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public String toUri() {
        return scheme + "://" + ip + ":" + String.valueOf(port);
    }

    @Override
    public String toString() {
        return "NeighborDto{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", active=" + active + '\'' +
                ", scheme='" + scheme + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port +
                '}';
    }
}
