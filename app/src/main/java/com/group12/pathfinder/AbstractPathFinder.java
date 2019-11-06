package com.group12.pathfinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public abstract class AbstractPathFinder {

    private static final Logger LOGGER =  LoggerFactory.getLogger(AbstractPathFinder.class);

    private final String origin;
    private final String destination;
    private final String url;
    private final String travelTime;
    private  String travelMode;

    public AbstractPathFinder(String origin, String destination,String url,String travelTime) {
        this.origin = origin;
        this.destination = destination;
        this.travelMode = "";
        this.url =  url;
        this.travelTime = travelTime;

    }
    public void setTravelMode(String travelMode){
        this.travelMode = travelMode;
    }

    public OutputStream makeRequest(String method, HashMap<String,String> parameters){
        try {
            URL requestUrl = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(method);
            LOGGER.info("Constructing connection to {[]}, using request method {[]}",requestUrl, method);
            return connection.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract AbstractDirectionsObject findPath();

    public String getUrl() {
        return url;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getTravelTime() {
        return travelTime;
    }
}
