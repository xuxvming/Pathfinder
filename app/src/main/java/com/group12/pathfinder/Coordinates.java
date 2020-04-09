package com.group12.pathfinder;

import java.io.Serializable;
import java.util.List;

public class Coordinates extends AbstractDirectionsObject implements Serializable {

    private List<LatLng> coordianteList;

    public Coordinates(List<LatLng> coordianteList){
        this.coordianteList = coordianteList;
    }
    public List<LatLng> getCoordiantelist() {
        return coordianteList;
    }

    public void setCoordiantelist(List<LatLng> coordiantelist) {
        this.coordianteList = coordiantelist;
    }
}

