package org.iotacontrolcenter.dto;


import java.util.ArrayList;
import java.util.List;

public class IccrIotaNeighborsPropertyDto extends IccrPropertyDto {

    private List<NeighborDto> neighbors;

    public IccrIotaNeighborsPropertyDto() {
    }

    public IccrIotaNeighborsPropertyDto(String key, List<NeighborDto> neighbors) {
        super(key, null);
        this.neighbors = neighbors;
    }

    // Convenience method, but don't want it to cause bean type getter/setter problems
    // with mapping to/from json
    public String nbrKeys() {
        String val = "";
        String sep = "";
        if(neighbors == null || neighbors.isEmpty()) {
            return val;
        }
        for(NeighborDto nbr : neighbors) {
            val += sep + nbr.getKey();
            if(sep.isEmpty()) {
                sep = ",";
            }
        }
        return val;
    }

    public List<NeighborDto> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<NeighborDto> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(NeighborDto nbr) {
        if(neighbors == null) {
            neighbors = new ArrayList<>();
        }
        neighbors.add(nbr);
    }

    @Override
    public String toString() {
        return "IccrIotaNeighborsPropertyDto{" +
                "neighbors=" + neighbors +
                '}';
    }
}
