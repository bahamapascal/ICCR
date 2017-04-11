package org.iotacontrolcenter.dto;

public class IccrPropertyDto {
    private String key;
    private String value;

    public IccrPropertyDto() { }

    public IccrPropertyDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean valueIsSuccess() {
        return value != null && !value.isEmpty() && value.toLowerCase().equals("true");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IccrPropertyDto)) return false;

        IccrPropertyDto that = (IccrPropertyDto) o;

        return getKey().equals(that.getKey()) && getValue().equals(that.getValue());

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