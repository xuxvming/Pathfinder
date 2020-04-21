package com.group12.pathfinder;

import com.chaquo.python.PyObject;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocalDirectionsObject extends AbstractDirectionsObject implements Serializable {

    private Map<String,List<String>> modes;
    private Map<String, TravelMode> availableModes;

    public void addAvailableModes(String travelChoice, List<PyObject> list) {
        List<String> mode = new LinkedList<>();
        for (PyObject object:list){
            mode.add(object.toString());
        }
        getModes().put(travelChoice,mode);
    }

    private List<List<GeoPoint>> addCoordinates(List<PyObject> list){
        List<List<GeoPoint>> coordinates = new LinkedList<>();
        for (PyObject object : list){
            List<PyObject> listObject  = object.asList();
            List<GeoPoint> tempList = new LinkedList<>();
            for (PyObject object1 : listObject){
                List<PyObject> coordinateTuple = object1.asList();
                double lat = coordinateTuple.get(0).toDouble();
                double lng = coordinateTuple.get(1).toDouble();
                GeoPoint point = new GeoPoint(lat,lng);
                tempList.add(point);
            }
            coordinates.add(tempList);
        }
        return coordinates;
    }

    public void addAvailableMethods(String travelChoice, List<PyObject> list){
        List<List<GeoPoint>> coordinates = addCoordinates(list);
        TravelMode mode = new TravelMode(coordinates);
        getAvailableModes().put(travelChoice,mode);
    }

    public LocalDirectionsObject(){
        this.modes = new HashMap<>();
        this.availableModes = new HashMap<>();
        super.setAvailableModes(availableModes);
        super.setModes(modes);
    }


}

