package com.group12.transport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group12.utils.RequestMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TramStops implements TransportFeilds{

    private final static Logger LOGGER =  LoggerFactory.getLogger(TramStops.class);

    public static List<AbstractTransportResponse> getTramStops(RequestMaker requestMaker){
        List<AbstractTransportResponse> res = new ArrayList<>();
        try {
            String jsonString = requestMaker.execute(LUAS_REQUEST_URL).get();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(jsonString);
            for (JsonElement object: jsonElement.getAsJsonArray()){
                JsonObject jsonObject = object.getAsJsonObject();
                AbstractTransportResponse response = new AbstractTransportResponse(jsonObject.get(LUAS_LATITUDE).getAsDouble(),
                        jsonObject.get(LUAS_LONGITUDE).getAsDouble(),
                        jsonObject.get(LUAS_LONG_NAME).getAsString());
                res.add(response);
            }
            return res;
        } catch (ExecutionException e) {
            LOGGER.error("Error making request",e);
        } catch (InterruptedException e) {
            LOGGER.error("Request Interrupted", e);
        }
        return null;
    }
}
