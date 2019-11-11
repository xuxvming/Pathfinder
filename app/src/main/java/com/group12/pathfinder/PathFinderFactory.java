package com.group12.pathfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PathFinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinderFactory.class);

    private final static String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private final static String WEB_SERVICE_API = "";
    private final static String META_DATA_KEY = "com.google.android.geo.API_KEY";
    private final Context context;


    public PathFinderFactory(Context context){
        this.context = context;
    }
    public AbstractPathFinder getPathFinder(String mode, String origin, String destination){
        if (mode.equals("P2P")){
            return null;
        }
        else {
            return new GoogleMapsPathFinder(origin,destination,GOOGLE_DIRECTIONS_API,getApiKey());
        }
    }

    public String getApiKey(){
        String apikey = "";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            LOGGER.info("Getting api key from [{}]",META_DATA_KEY);
            apikey = bundle.getString(META_DATA_KEY);

        } catch (PackageManager.NameNotFoundException e) {
            LOGGER.error("Error getting api key",e );
        }
        return apikey;
    }
}
