package org.iotacontrolcenter.dto;


import java.util.List;

public class IotaGetNeighborsResponseDto {

    private int duration;
    private List<IotaNeighborDto> neighbors;

    public IotaGetNeighborsResponseDto() { }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<IotaNeighborDto> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<IotaNeighborDto> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public String toString() {
        return "IotaGetNeighborsResponseDto{" +
                "duration=" + duration +
                ", neighbors=" + neighbors +
                '}';
    }
}
