package com.group12.pathfinder;

import android.content.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class PathFinderFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinderFactory.class);

    private final static String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private final static String WEB_SERVICE_API = "";
    private final static String API_KEY = "AIzaSyDSD2X4p1lAIuYGWTcr7NKKPNPTAgsUD-w";

    private transient Context context;
    private String origin;
    private String destination;
    private String mode = "GoogleMaps";
    private String graph_location;


    public AbstractPathFinder getPathFinder(){
        if (mode.equals("P2P")){
            return new P2PPathFinder(getOrigin(),getDestination(),"", graph_location);
        }
        else {
            return new GoogleMapsPathFinder(getOrigin(),getDestination(),GOOGLE_DIRECTIONS_API,API_KEY);
        }
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    private String getOrigin() {
        return origin;
    }

    void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private String getDestination() {
        return destination;
    }

    public String getGraph_location() {
        return graph_location;
    }

    public void setGraph_location(String graph_location) {
        this.graph_location = graph_location;
    }
}
