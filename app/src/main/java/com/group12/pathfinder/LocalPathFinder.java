package com.group12.pathfinder;


import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.group12.utils.RequestMaker;
import org.osmdroid.util.GeoPoint;

import java.util.Map;

public class LocalPathFinder extends AbstractPathFinder {
    private String cachedFile;
    private int travelChoice;
    LocalPathFinder(GeoPoint origin, GeoPoint destination, String url, String cachedFile, int travelChoice) {
        super(origin, destination, "");
        this.cachedFile = cachedFile;
        this.travelChoice = travelChoice;
    }

    @Override
    public String createURl() {
        return null;
    }

    @Override
    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker) {
        return getCoordinates();
    }

    public LocalDirectionsObject getCoordinates(){
        Log.i("file stored in: ",cachedFile);
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("android_script");
        PyObject res = pythonFile.callAttr("get_coordinates", new double[]{getOrigin().getLatitude(),getOrigin().getLongitude()}, new double[]{getDestination().getLatitude(), getDestination().getLongitude()},travelChoice,cachedFile);
        Map<PyObject,PyObject> coordinatesList = res.asMap();
        LocalDirectionsObject directionsObject = new LocalDirectionsObject();
        for (PyObject key: coordinatesList.keySet()){
            String method = key.toString();
            Map<PyObject,PyObject> temp = coordinatesList.get(key).asMap();
            for (PyObject subKey: temp.keySet()){
                if (subKey.toString().equals("modes")){
                    directionsObject.addAvailableModes(method,temp.get(subKey).asList());
                }
                if (subKey.toString().equals("coordinates")){
                    directionsObject.addAvailableMethods(method,temp.get(subKey).asList());
                }
            }
        }
        directionsObject.setStartPoint(getOrigin());
        directionsObject.setEndPoint(getDestination());
        return directionsObject;
    }
}

