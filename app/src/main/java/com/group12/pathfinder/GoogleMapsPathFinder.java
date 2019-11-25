package com.group12.pathfinder;

import java.util.HashMap;

public class GoogleMapsPathFinder extends AbstractPathFinder {

    private String apiKey;

    public GoogleMapsPathFinder(String origin, String destination, String url,String apiKey) {
        super(origin, destination, url);
        this.apiKey = apiKey;
    }

    @Override
    public String createURl() {
        RequestParams params = new RequestParams();
        params.put("origin",getOrigin());
        params.put("destination",getDestination());
        params.put("key",apiKey);
        String url = getUrl()+params.toString();
        return url;
    }
}

