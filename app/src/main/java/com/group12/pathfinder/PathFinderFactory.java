package com.group12.pathfinder;

import android.content.Context;
import org.osmdroid.util.GeoPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class PathFinderFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinderFactory.class);

    private final static String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";

    private final static String WEB_SERVICE_API = "http://104.197.46.193/getcoords?";
    private final static String API_KEY = "AIzaSyDSD2X4p1lAIuYGWTcr7NKKPNPTAgsUD-w";

    private transient Context context;

    private GeoPoint originLatLng;


    private GeoPoint destinationLatLng;
    private String source = "Server";
    private String graph_location;
    private int travelChoice = 0;
    private String searchText = "";

    public AbstractPathFinder getPathFinder(){
        if (source.equals("Local")){
            return new LocalPathFinder(originLatLng,destinationLatLng,"",graph_location,travelChoice);
        }
        else {
            return new OnlinePathFinder(originLatLng,destinationLatLng,WEB_SERVICE_API,travelChoice);
        }
    }


    public String getSource() {
        return source;
    }

    public void setSource(String mode) {
        this.source = mode;
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

    public String getSearchText(){return searchText;}

    public void setSearchText(String text){this.searchText = text;}

    public GeoPoint getDestinationLatLng() {
        return destinationLatLng;
    }

    public static String getWebServiceApi() {
        return WEB_SERVICE_API;
    }


}
