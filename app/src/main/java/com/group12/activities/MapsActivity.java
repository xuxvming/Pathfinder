package com.group12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.LocationListnerImpl;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.permission.PermissionChecker;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton locationButton;
    private FloatingActionButton searchButton;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(!PermissionChecker.checkPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
            PermissionChecker.requestPermission(MapsActivity.this,MY_PERMISSIONS_REQUEST_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
        }
        mapFragment.getMapAsync(this);
        locationButton = findViewById(R.id.location_button);
        searchButton = findViewById(R.id.search_button);
        final PathFinderFactory factory = new PathFinderFactory();
        factory.setContext(MapsActivity.this);
        locationButton.setOnClickListener(new OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListnerImpl(mMap,factory));
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(MapsActivity.this,SearchActivity.class);
                                                intent.putExtra("PathFinderFactory",factory);
                                                startActivity(intent);
                                            }
                                        }
        );

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        AbstractDirectionsObject response = (AbstractDirectionsObject) getIntent().getSerializableExtra("Response");
        if (response != null){
            addMarkersToMap(response,mMap);
            positionCamera(response,mMap);
            addPolyline(response,mMap);
        }
    }

    private void addMarkersToMap(AbstractDirectionsObject results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.getOriginLat(), results.getOriginLng())));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.getDestinationLat(), results.getDestinationLng())));
    }

    private void positionCamera(AbstractDirectionsObject results, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(results.getOriginLat(), results.getOriginLng()), 12));
    }

    private void addPolyline(AbstractDirectionsObject results, GoogleMap mMap) {
        String polyline = results.getOverviewPolyline();
        List<LatLng> decoded = PolyUtil.decode(polyline);
        mMap.addPolyline(new PolylineOptions().addAll(decoded));
    }
}
