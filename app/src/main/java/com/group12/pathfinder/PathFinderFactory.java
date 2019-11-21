package com.group12.pathfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class PathFinderFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinderFactory.class);

    private final static String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private final static String WEB_SERVICE_API = "";
    private final static String META_DATA_KEY = "com.google.android.geo.API_KEY";

    private Context context;
    private String origin;
    private String destination;
    private String mode;


    public AbstractPathFinder getPathFinder(){
        if (mode.equals("P2P")){
            return null;
        }
        else {
            return new GoogleMapsPathFinder(getOrigin(),getDestination(),GOOGLE_DIRECTIONS_API,getApiKey());
        }
    }

    public String getApiKey(){
        String apikey = "";
        try {
            ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            LOGGER.info("Getting api key from [{}]",META_DATA_KEY);
            apikey = bundle.getString(META_DATA_KEY);

        } catch (PackageManager.NameNotFoundException e) {
            LOGGER.error("Error getting api key",e );
        }
        return apikey;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
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
    public String getDestination() {
        return destination;
    }


}
