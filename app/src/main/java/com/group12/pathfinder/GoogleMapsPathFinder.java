package com.group12.pathfinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group12.utils.RequestMaker;
import com.group12.utils.RequestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class GoogleMapsPathFinder extends AbstractPathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleMapsPathFinder.class);

    private final String apiKey;

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

    @Override
    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker){
        AbstractDirectionsObject abstractDirectionsObject = new AbstractDirectionsObject();
        try {
            String res = requestMaker.execute(createURl()).get();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(res);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray routes = jsonObject.getAsJsonArray("routes");
            if (routes.size() == 0){
                return abstractDirectionsObject;
            }
            String polyline = routes.get(0).getAsJsonObject().get("overview_polyline").getAsJsonObject().get("points").getAsString();
            JsonObject starLocation = (JsonObject) routes.get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("start_location");
            JsonObject endLocation = (JsonObject) routes.get(0).getAsJsonObject().get("legs").getAsJsonArray().get(0).getAsJsonObject().get("end_location");
            abstractDirectionsObject.setOverviewPolyline(polyline);
            abstractDirectionsObject.setOriginLat(starLocation.get("lat").getAsDouble());
            abstractDirectionsObject.setOriginLng(starLocation.get("lng").getAsDouble());
            abstractDirectionsObject.setDestinationLat(endLocation.get("lat").getAsDouble());
            abstractDirectionsObject.setDestinationLng(endLocation.get("lng").getAsDouble());
        } catch (ExecutionException e ) {
            LOGGER.error("Error getting request",e);
        } catch (InterruptedException e) {
            LOGGER.error("Request Interrupted",e);
        }
        return abstractDirectionsObject;
    }


}

