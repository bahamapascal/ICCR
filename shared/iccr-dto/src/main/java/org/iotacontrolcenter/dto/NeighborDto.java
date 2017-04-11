package org.iotacontrolcenter.dto;


public class NeighborDto {

    private boolean active;
    private String description;
    private String key;
    private String name;
    private String uri;
    private int numAt = 0;
    private int numIt = 0;
    private int numNt = 0;

    public NeighborDto() {
    }

    public NeighborDto(String key, String uri, String name, String description,
                       boolean active) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.active = active;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getNumAt() {
        return numAt;
    }

    public void setNumAt(int numAt) {
        this.numAt = numAt;
    }

    public int getNumIt() {
        return numIt;
    }

    public void setNumIt(int numIt) {
        this.numIt = numIt;
    }

    public int getNumNt() {
        return numNt;
    }

    public void setNumNt(int numNt) {
        this.numNt = numNt;
    }

    @Override
    public String toString() {
        return "NeighborDto{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + description + '\'' +
                ", active=" + active + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeighborDto)) return false;

        NeighborDto that = (NeighborDto) o;

        return getUri().equals(that.getUri()) && getKey().equals(that.getKey());
    }

    @Override
    public int hashCode() {
        int result = getUri().hashCode();
        result = 31 * result + getKey().hashCode();
        return result;
    }
}
