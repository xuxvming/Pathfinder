package com.group12.pathfinder;

import com.group12.utils.ResponseObject;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.*;

public class AbstractDirectionsObject extends ResponseObject {
    private String overviewPolyline;
    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;

    private GeoPoint endPoint;
    private GeoPoint startPoint;

    private Map<String,List<String>> modes;
    private Map<String, TravelMode> availableModes;

    private int travelChoice;

    public AbstractDirectionsObject(){
        this.availableModes = new HashMap<>();
        this.modes = new HashMap<>();
    }

    public Map<String, List<List<GeoPoint>>>getAvailableRoute(String travelChoice) {
        Map<String,List<List<GeoPoint>>> res = new HashMap<>();
        List<String> mode = modes.get(travelChoice);
        if (availableModes.containsKey(travelChoice)){
            TravelMode travelMode = availableModes.get(travelChoice);
            List<List<GeoPoint>> coordinates = travelMode.getCoordinatesWithMode();
            for (int i =0; i<mode.size();i++){
                if (res.containsKey(mode.get(i))){
                    res.get(mode.get(i)).add(coordinates.get(i));
                }else{
                    List<List<GeoPoint>> lists = new LinkedList<>();
                    lists.add(coordinates.get(i));
                    res.put(mode.get(i),lists);
                }
            }
        }
        return res;
    }

    public Set<String> getAvailableRoutes() {
        return availableModes.keySet();
    }

    public void setModes(Map<String,List<String>> modes) {
        this.modes = modes;
    }

    public void setAvailableModes(Map<String, TravelMode> availableModes) {
        this.availableModes = availableModes;
    }

    public Map<String,List<String>> getModes() {
        return modes;
    }

    public Map<String, TravelMode> getAvailableModes() {
        return availableModes;
    }

    public GeoPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(GeoPoint startPoint) {
        this.startPoint = startPoint;
    }

    public GeoPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(GeoPoint endPoint) {
        this.endPoint = endPoint;
    }

    public int getTravelChoice() {
        return travelChoice;
    }

    public void setTravelChoice(int travelChoice) {
        this.travelChoice = travelChoice;
    }

    static class TravelMode implements Serializable {
        private List<List<GeoPoint>> coordinatesWithMode;
        public TravelMode(List<List<GeoPoint>> coordinatesWithMode){
            this.coordinatesWithMode = coordinatesWithMode;
        }

        public List<List<GeoPoint>> getCoordinatesWithMode() {
            return coordinatesWithMode;
        }

        public void setCoordinatesWithMode(List<GeoPoint> coordinates) {
            coordinatesWithMode.add(coordinates);
        }
    }

    public void setOverviewPolyline(String overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getOverviewPolyline() {
        return overviewPolyline;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

}
