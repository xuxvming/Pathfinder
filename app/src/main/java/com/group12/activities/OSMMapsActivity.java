package com.group12.activities;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.app.Activity;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.DefaultConfigurationProvider;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;


import androidx.fragment.app.FragmentActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class OSMMapsActivity extends Activity {
    public static final String TAG = "Eoin_Test_One";
    MapView map = null;
    private MyLocationNewOverlay mMyLocationOverlay;
    IMapController mapController;
    private FusedLocationProviderClient fusedLocationclient;
    Location curr_loc = new Location("dummyprovider");
    Location LastKnownLocation = null;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_osm_maps);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(9.5);
        fusedLocationclient = LocationServices.getFusedLocationProviderClient(this);
        Task<LocationAvailability> test = fusedLocationclient.getLocationAvailability();
        getDeviceLocation();
    }

    public void onResume(){
        super.onResume();
        map.onResume();
    }

    public void onPause(){
        super.onPause();
        map.onPause();
    }

    public void onMapReady(MapView map){
        getDeviceLocation();
    }
    private void getDeviceLocation() {
        try {
                Task<Location> locationResult = fusedLocationclient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>(){
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            LastKnownLocation = task.getResult();
                            GeoPoint startPoint = new GeoPoint(LastKnownLocation.getLatitude(), LastKnownLocation.getLongitude());
                            setMapView(startPoint);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            GeoPoint startPoint = new GeoPoint(54.23134, 64.13213);
                            setMapView(startPoint);
                        }
                    }
                });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updatetLastKnownLocation(){
        Task<Location> locationResult = fusedLocationclient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    LastKnownLocation = task.getResult();
                }
            }
        });
    }
    private void setMapView(GeoPoint point){
        mapController.setCenter(point);
    }
}


