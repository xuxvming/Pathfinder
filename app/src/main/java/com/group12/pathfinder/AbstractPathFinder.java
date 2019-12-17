package com.group12.pathfinder;

import com.group12.utils.RequestMaker;


public abstract class AbstractPathFinder {

    private final String origin;
    private final String destination;
    private final String url;
    private String departureTime;
    private String mode;

    AbstractPathFinder(String origin, String destination, String url) {
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

    String getOrigin() { return origin; }

    String getDestination() { return destination; }

    String getDepartureTime() { return departureTime; }

    String getMode() { return mode; }

    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

}


