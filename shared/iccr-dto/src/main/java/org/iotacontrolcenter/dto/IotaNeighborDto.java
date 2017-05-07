package org.iotacontrolcenter.dto;

public class IotaNeighborDto {

    private String address;
    private int numberOfAllTransactions;
    private int numberOfNewTransactions;
    private int numberOfInvalidTransactions;
    private int activityPercentageDay;
    private int activityPercentageWeek;

    public IotaNeighborDto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IotaNeighborDto)) {
            return false;
        }

        IotaNeighborDto that = (IotaNeighborDto) o;

        return getAddress().equals(that.getAddress());

    }

    public int getActivityPercentageDay() {
        return activityPercentageDay;
    }

    public int getActivityPercentageWeek() {
        return activityPercentageWeek;
    }

    public String getAddress() {
        return address;
    }

    public int getNumberOfAllTransactions() {
        return numberOfAllTransactions;
    }

    public int getNumberOfInvalidTransactions() {
        return numberOfInvalidTransactions;
    }

    public int getNumberOfNewTransactions() {
        return numberOfNewTransactions;
    }

    @Override
    public int hashCode() {
        return getAddress().hashCode();
    }

    public void setActivityPercentageDay(int activityPercentageDay) {
        this.activityPercentageDay = activityPercentageDay;
    }

    public void setActivityPercentageWeek(int activityPercentageWeek) {
        this.activityPercentageWeek = activityPercentageWeek;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumberOfAllTransactions(int numberOfAllTransactions) {
        this.numberOfAllTransactions = numberOfAllTransactions;
    }

    public void setNumberOfInvalidTransactions(int numberOfInvalidTransactions) {
        this.numberOfInvalidTransactions = numberOfInvalidTransactions;
    }

    public void setNumberOfNewTransactions(int numberOfNewTransactions) {
        this.numberOfNewTransactions = numberOfNewTransactions;
    }

    @Override
    public String toString() {
        return "IotaNeighborDto{" +
                "address='" + address + '\'' +
                ", numberOfAllTransactions=" + numberOfAllTransactions +
                ", numberOfNewTransactions=" + numberOfNewTransactions +
                ", numberOfInvalidTransactions=" + numberOfInvalidTransactions +
                ", activityPercentageDay=" + activityPercentageDay +
                ", activityPercentageWeek=" + activityPercentageWeek +
                '}';
    }
}
