package com.group12.pathfinder;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationListenerImpl implements LocationListener {
    private final GoogleMap googleMaps;
    private final PathFinderFactory factory;
    Logger LOGGER = LoggerFactory.getLogger(LocationListenerImpl.class);

    public LocationListenerImpl(GoogleMap map, PathFinderFactory factory){
        this.googleMaps = map;
        this.factory = factory;
    }


    @Override
    public void onLocationChanged(Location location) {
       LOGGER.info("Updating locations");
       LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
       String origin = currLocation.latitude + "," + currLocation.longitude;
       factory.setOrigin(origin);
       googleMaps.addMarker(new MarkerOptions().position(currLocation));
       googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation,12.0f));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
