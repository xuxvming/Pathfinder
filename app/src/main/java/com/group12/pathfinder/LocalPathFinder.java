package com.group12.pathfinder;


import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.group12.utils.RequestMaker;
import org.osmdroid.util.GeoPoint;

import java.util.Map;

public class LocalPathFinder extends AbstractPathFinder {
    private String TAG = LocalPathFinder.class.getName();
    private String cachedFile;

    public LocalPathFinder(GeoPoint origin, GeoPoint destination, String url, String cachedFile, int travelChoice) {
        super(origin, destination, "",travelChoice);
        this.cachedFile = cachedFile;
    }

    @Override
    public String createURl() {
        return "";
    }

    @Override
    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker) {
        return getCoordinates();
    }

    public LocalDirectionsObject getCoordinates(){
        Log.e(TAG,"Connection not available");
        Log.i(TAG,"Using local service with graph file stored in: "+cachedFile);
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("android_script");
        PyObject res = pythonFile.callAttr("get_coordinates", new double[]{getOrigin().getLatitude(),getOrigin().getLongitude()}, new double[]{getDestination().getLatitude(), getDestination().getLongitude()},getTravelChoice(),cachedFile);
        Map<PyObject,PyObject> coordinatesList = res.asMap();
        LocalDirectionsObject directionsObject = new LocalDirectionsObject();
        for (PyObject key: coordinatesList.keySet()){
            String method = key.toString();
            Map<PyObject,PyObject> temp = coordinatesList.get(key).asMap();
            for (PyObject subKey: temp.keySet()){
                if (subKey.toString().equals("mode")){
                    directionsObject.addAvailableModes(method,temp.get(subKey).asList());
                }
                if (subKey.toString().equals("coordinates")){
                    directionsObject.addAvailableMethods(method,temp.get(subKey).asList());
                }
            }
        }
        directionsObject.setStartPoint(getOrigin());
        directionsObject.setEndPoint(getDestination());
        directionsObject.setTravelChoice(getTravelChoice());
        return directionsObject;
    }
}

