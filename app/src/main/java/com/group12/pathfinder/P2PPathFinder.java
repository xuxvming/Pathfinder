package com.group12.pathfinder;


import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.group12.utils.RequestMaker;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class P2PPathFinder extends AbstractPathFinder {
    private String cachedFile;
    private int travelChoice;
    P2PPathFinder(GeoPoint origin, GeoPoint destination, String url, String cachedFile, int travelChoice) {
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

    public Coordinates getCoordinates(){
        Log.i("file stored in: ",cachedFile);
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("script");
        PyObject res = pythonFile.callAttr("get_coordinates", new double[]{getOrigin().getLatitude(), getOrigin().getLongitude()}, new double[]{53.3585859, -6.2355241},travelChoice,cachedFile);
        List<PyObject> coordinatesList = res.asList();
        List<GeoPoint> list = new ArrayList<>();
        int i = 0;
        int j = 1;
        while (j < coordinatesList.size()){
            double lat = coordinatesList.get(i).toDouble();
            double lng = coordinatesList.get(j).toDouble();
            GeoPoint latLng = new GeoPoint(lat,lng);
            list.add(latLng);
            j++;
            i++;
        }
        return new Coordinates(list);
    }
}

