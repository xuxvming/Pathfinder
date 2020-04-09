package com.group12.activities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


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
                            try {
                                GeoPoint startPoint = new GeoPoint(LastKnownLocation.getLatitude(), LastKnownLocation.getLongitude());
                                setMapView(startPoint);
                            }catch (NullPointerException e ){
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                GeoPoint startPoint = new GeoPoint(54.23134, 64.13213);
                                setMapView(startPoint);
                            }
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


