package org.iotacontrolcenter.dto;

public class IccrPropertyDto {
    private String key;
    private Object value;

    public IccrPropertyDto() { }

    public IccrPropertyDto(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IccrPropertyDto)) return false;

        IccrPropertyDto that = (IccrPropertyDto) o;

        if (!getKey().equals(that.getKey())) return false;
        return getValue().equals(that.getValue());

    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IccrPropertyDto{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}