package org.iotacontrolcenter.dto;


import java.util.ArrayList;
import java.util.List;

public class IccrIotaNeighborsPropertyDto extends IccrPropertyDto {

    private List<NeighborDto> nbrs;

    public IccrIotaNeighborsPropertyDto() {
    }

    public IccrIotaNeighborsPropertyDto(String key, List<NeighborDto> nbrs) {
        super(key, null);
        this.nbrs = nbrs;
    }

    // Convenience method, but don't want it to cause bean type getter/setter problems
    // with mapping to/from json
    public String nbrKeys() {
        String val = "";
        String sep = "";
        if(nbrs == null || nbrs.isEmpty()) {
            return val;
        }
        for(NeighborDto nbr : nbrs) {
            val += sep + nbr.getKey();
            if(sep.isEmpty()) {
                sep = ",";
            }
        }
        return val;
    }

    public List<NeighborDto> getNbrs() {
        return nbrs;
    }

    public void setNbrs(List<NeighborDto> nbrs) {
        this.nbrs = nbrs;
    }

    public void addNeighbor(NeighborDto nbr) {
        if(nbrs == null) {
            nbrs = new ArrayList<>();
        }
        nbrs.add(nbr);
    }

    @Override
    public String toString() {
        return "IccrIotaNeighborsPropertyDto{" +
                "nbrs=" + nbrs +
                '}';
    }
}
