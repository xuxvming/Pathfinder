package com.group12.pathfinder;


import android.util.Log;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.group12.utils.RequestMaker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class P2PPathFinder extends AbstractPathFinder {
    private String cachedFile;
    private static final String GRAPH_FILE_NAME = "graph_with_bus_luas_linked.p";
    P2PPathFinder(String origin, String destination, String url, String context) {
        super(origin, destination, "");
        this.cachedFile = context;
    }

    @Override
    public String createURl() {
        return null;
    }

    @Override
    public AbstractDirectionsObject makeRequest(RequestMaker requestMaker) {
        return test();
    }

    public Coordinates test(){
        Log.i("file stored in: ",cachedFile);
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("script");
        PyObject res = pythonFile.callAttr("get_coordinates", new double[]{53.3078264, -6.3435349}, new double[]{53.3585859, -6.2355241},"bus","luas",cachedFile);
        List<PyObject> coordinatesList = res.asList();
        List<LatLng> list = new ArrayList<>();
        int i = 0;
        int j = 1;
        while (j < coordinatesList.size()){
            double lat = coordinatesList.get(i).toDouble();
            double lng = coordinatesList.get(j).toDouble();
            LatLng latLng = new LatLng(lat,lng);
            list.add(latLng);
            j++;
            i++;
        }
        return new Coordinates(list);
    }
}

class LatLng implements Serializable {
    private  double lat;
    private  double lng;

    LatLng(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }
    LatLng(){

    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
