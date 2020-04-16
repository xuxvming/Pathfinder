package com.group12.pathfinder;

import android.content.Context;
import org.osmdroid.util.GeoPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class PathFinderFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinderFactory.class);

    private final static String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private final static String WEB_SERVICE_API = "";
    private final static String API_KEY = "AIzaSyDSD2X4p1lAIuYGWTcr7NKKPNPTAgsUD-w";

    private transient Context context;

    private GeoPoint originLatLng;
    private GeoPoint destinationLatLng;
    private String mode = "GoogleMaps";
    private String graph_location;
    private int travelChoice = 2;


    public AbstractPathFinder getPathFinder(){
        if (mode.equals("P2P")){
            return new P2PPathFinder(originLatLng,destinationLatLng,"",graph_location,travelChoice);
        }
        else {
            return new GoogleMapsPathFinder(originLatLng,destinationLatLng,GOOGLE_DIRECTIONS_API,API_KEY);
        }
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

    public GeoPoint getOriginLatLng() {
        return originLatLng;
    }

    public void setOriginLatLng(GeoPoint originLatLng) {
        this.originLatLng = originLatLng;
    }

    public String getGraph_location() {
        return graph_location;
    }

    public void setGraph_location(String graph_location) {
        this.graph_location = graph_location;
    }

    public void setTravelChoice(int travelChoice) {
        this.travelChoice = travelChoice;
    }

    public void setDestinationLatLng(GeoPoint destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }
}
