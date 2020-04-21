package com.group12.pathfinder;

import com.group12.utils.RequestMaker;
import org.osmdroid.util.GeoPoint;


public abstract class AbstractPathFinder {

    private final GeoPoint origin;
    private final GeoPoint destination;
    private final String url;
    private int travelChoice;

    AbstractPathFinder(GeoPoint origin, GeoPoint destination,String url, int travelChoice) {
        this.origin = origin;
        this.destination = destination;
        this.url =  url;
        this.travelChoice = travelChoice;
    }

    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker){
        return  null;
    }

    public abstract String createURl();

    String getUrl() {
        return url;
    }

    GeoPoint getOrigin() { return origin; }

    GeoPoint getDestination() { return destination; }

    public int getTravelChoice() {
        return travelChoice;
    }

    public void setTravelChoice(int travelChoice) {
        this.travelChoice = travelChoice;
    }
}


