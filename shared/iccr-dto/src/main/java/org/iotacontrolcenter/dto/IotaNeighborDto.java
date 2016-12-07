package org.iotacontrolcenter.dto;

public class IotaNeighborDto {

    private String address;
    private int numberOfAllTransactions;
    private int numberOfNewTransactions;
    private int numberOfInvalidTransactions;

    public IotaNeighborDto() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfAllTransactions() {
        return numberOfAllTransactions;
    }

    public void setNumberOfAllTransactions(int numberOfAllTransactions) {
        this.numberOfAllTransactions = numberOfAllTransactions;
    }

    public int getNumberOfNewTransactions() {
        return numberOfNewTransactions;
    }

    public void setNumberOfNewTransactions(int numberOfNewTransactions) {
        this.numberOfNewTransactions = numberOfNewTransactions;
    }

    public int getNumberOfInvalidTransactions() {
        return numberOfInvalidTransactions;
    }

    public void setNumberOfInvalidTransactions(int numberOfInvalidTransactions) {
        this.numberOfInvalidTransactions = numberOfInvalidTransactions;
    }

    @Override
    public String toString() {
        return "IotaNeighborDto{" +
                "address='" + address + '\'' +
                ", numberOfAllTransactions=" + numberOfAllTransactions +
                ", numberOfNewTransactions=" + numberOfNewTransactions +
                ", numberOfInvalidTransactions=" + numberOfInvalidTransactions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IotaNeighborDto)) return false;

        IotaNeighborDto that = (IotaNeighborDto) o;

        return getAddress().equals(that.getAddress());

    }

    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }
}
