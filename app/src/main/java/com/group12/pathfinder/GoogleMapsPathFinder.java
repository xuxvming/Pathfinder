package com.group12.pathfinder;

import java.util.HashMap;

public class GoogleMapsPathFinder extends AbstractPathFinder {
    private final String requestMethod = "GET";
    private final String API_KEY;

    public GoogleMapsPathFinder(String origin, String destination, String url, String travelTime, String api_key) {
        super(origin, destination, url, travelTime);
        API_KEY = api_key;
    }

    @Override
    public AbstractDirectionsObject findPath() {
        HashMap<String,String> params = new HashMap<>();
        return null;
    }

}
