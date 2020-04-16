package com.group12.pathfinder;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class Coordinates extends AbstractDirectionsObject implements Serializable {

    private List<GeoPoint> coordianteList;

    public Coordinates(List<GeoPoint> coordianteList){
        this.coordianteList = coordianteList;
    }
    public List<GeoPoint> getCoordiantelist() {
        return coordianteList;
    }

    public void setCoordiantelist(List<GeoPoint> coordiantelist) {
        this.coordianteList = coordiantelist;
    }
}

