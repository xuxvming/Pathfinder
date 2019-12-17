package com.group12.transport;

import com.group12.utils.ResponseObject;

public class AbstractTransportResponse extends ResponseObject {

    private double latitude;
    private double longitude;
    private String stopName;

    public AbstractTransportResponse(double latitude, double longitude,String stopName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.stopName = stopName;
    }


    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
