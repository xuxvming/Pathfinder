package com.group12.pathfinder;

import com.group12.utils.RequestMaker;
import org.osmdroid.util.GeoPoint;


public abstract class AbstractPathFinder {

    private final GeoPoint origin;
    private final GeoPoint destination;
    private final String url;
    private String departureTime;
    private String mode;

    AbstractPathFinder(GeoPoint origin, GeoPoint destination, String url) {
        this.origin = origin;
        this.destination = destination;
        this.url =  url;
    }

    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker){
        return null;
    }

    public abstract String createURl();

    String getUrl() {
        return url;
    }

    GeoPoint getOrigin() { return origin; }

    GeoPoint getDestination() { return destination; }

    String getDepartureTime() { return departureTime; }

    String getMode() { return mode; }

    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

}


