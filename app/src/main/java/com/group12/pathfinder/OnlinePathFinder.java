package com.group12.pathfinder;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group12.utils.RequestMaker;
import com.group12.utils.RequestParams;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class OnlinePathFinder extends AbstractPathFinder {
    private String TAG = OnlinePathFinder.class.getName();
    OnlinePathFinder(GeoPoint origin, GeoPoint destination, String url,int travelChoice) {
        super(origin, destination, url,travelChoice);
    }

    @Override
    public String createURl() {
        //TODO: need to check request parameter
        RequestParams requestParams = new RequestParams();
        requestParams.put("latstart",getOrigin().getLatitude());
        requestParams.put("longstart",getOrigin().getLongitude());
        requestParams.put("latend",getDestination().getLatitude());
        requestParams.put("longend",getDestination().getLongitude());
        requestParams.put("case",getTravelChoice());
        return getUrl()+requestParams.toString();
    }

    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker){
        requestMaker.setRequestMethod("GET");
        AbstractDirectionsObject object = new AbstractDirectionsObject();
        Map<String, List<String>> modes = new HashMap<>();
        Map<String, AbstractDirectionsObject.TravelMode> availableModes = new HashMap<>();
        try {
            String res = requestMaker.execute(createURl()).get();
            Log.i(TAG,"Sending request to " + createURl());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> jsonMap =  objectMapper.readValue(res,Map.class);

            for (String key:jsonMap.keySet()){
                Map<String,Object> tempMap = (Map<String, Object>) jsonMap.get(key);
                for (String subKey: tempMap.keySet()){
                    if (subKey.equals("mode")){
                        modes.put(key, (List<String>) tempMap.get(subKey));
                    }
                    if (subKey.equals("coordinates")){
                        updateAvailableMode(key, (List<List<List<Double>>>) tempMap.get(subKey),availableModes);
                    }
                }
            }
            object.setAvailableModes(availableModes);
            object.setModes(modes);
            object.setEndPoint(getDestination());
            object.setStartPoint(getOrigin());
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    private List<List<GeoPoint>> addCoordinates(List<List<List<Double>>> coordinates){
        List<List<GeoPoint>> res = new LinkedList<>();
        for (List<List<Double>> coordinate:coordinates){
            List<GeoPoint> tempList = new LinkedList<>();
            for (List<Double> temp: coordinate){
                double lat = temp.get(0);
                double lng = temp.get(1);
                GeoPoint point = new GeoPoint(lat,lng);
                tempList.add(point);
            }
            res.add(tempList);
        }
        return res;
    }

    private void updateAvailableMode(String travelChoice, List<List<List<Double>>> coordinates, Map<String, AbstractDirectionsObject.TravelMode> availableModes){
        Map<String, AbstractDirectionsObject.TravelMode> res = new HashMap<>();
        AbstractDirectionsObject.TravelMode mode = new AbstractDirectionsObject.TravelMode(addCoordinates(coordinates));
        availableModes.put(travelChoice,mode);
    }

}
