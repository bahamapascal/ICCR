package org.iotacontrolcenter.dto;


import java.util.ArrayList;
import java.util.List;

public class IccrIotaNeighborsPropertyDto extends IccrPropertyDto {

    private List<NeighborDto> nbrs;

    public IccrIotaNeighborsPropertyDto(String key, List<NeighborDto> nbrs) {
        super(key, nbrs);
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
