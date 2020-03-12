package com.group12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;
import com.group12.p2p.FileTransferService;
import com.group12.p2p.WifiDirectService;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.LocationListenerImpl;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.transport.AbstractTransportResponse;
import com.group12.transport.TramStops;
import com.group12.utils.PermissionChecker;
import com.group12.utils.RequestMaker;
import com.group12.utils.ResponseObject;
import com.group12.p2p.WifiDirectService.MyLocalBinder;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton locationButton;
    private FloatingActionButton searchButton;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final PathFinderFactory factory = new PathFinderFactory();

    WifiDirectService wifiDirectService;
    boolean isBound = false;
    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            wifiDirectService = binder.getService();
            isBound = true;
            setupWifiDirectService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public void setupWifiDirectService()
    {
        WifiP2pManager manager = wifiDirectService.initialiseWifiService(this);
        if(manager == null){
            Log.d("MapsActivity", "Hueston we have a problem");
        }
    }


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
        final FloatingActionButton p2pButton = findViewById(R.id.p2p);


        factory.setContext(MapsActivity.this);

        searchButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(MapsActivity.this,SearchActivity.class);
                                                factory.setMode("GoogleMaps");
                                                intent.putExtra(PathFinderFactory.class.getName(),factory);
                                                startActivity(intent);
                                            }
                                        }
        );
        Intent intent = new Intent(this, WifiDirectService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        p2pButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDirectService.startDiscovery();
                TextView myTextView = (TextView) findViewById(R.id.myTextView);
            }
        });
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
            if (response.getOverviewPolyline()!=null){
                addMarkersToMap(response,mMap);
                addPolyline(response,mMap);
            }else{
                Toast.makeText(getApplicationContext(),"Path Not Found :(",Toast.LENGTH_SHORT).show();
            }
        }

        locationButton.setOnClickListener(new OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListenerImpl(mMap, factory));
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListenerImpl(mMap, factory));
                }
            }

        });
        locationButton.performClick();
        List<AbstractTransportResponse> luasStops = TramStops.getTramStops(new RequestMaker());
        assert luasStops != null;
        positionCamera(new LatLng(luasStops.get(0).getLatitude(),luasStops.get(0).getLongitude()),mMap);
        for (AbstractTransportResponse response1: luasStops){
            addMarkersToMap(response1,mMap);
        }

    }

    private void addMarkersToMap(ResponseObject results, GoogleMap mMap) {
        if (results instanceof AbstractDirectionsObject){
            AbstractDirectionsObject result = (AbstractDirectionsObject) results;
            mMap.addMarker(new MarkerOptions().position(new LatLng(result.getOriginLat(), result.getOriginLng())));
            mMap.addMarker(new MarkerOptions().position(new LatLng(result.getDestinationLat(), result.getDestinationLng())));
            positionCamera(new LatLng(result.getOriginLat(),result.getOriginLng()),mMap);
        }
        if (results instanceof AbstractTransportResponse){
            AbstractTransportResponse result = (AbstractTransportResponse) results;
            mMap.addMarker(new MarkerOptions().title(result.getStopName()).position(new LatLng(result.getLatitude(),result.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }
    }

    private void positionCamera(LatLng position, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
    }

    private void addPolyline(AbstractDirectionsObject results, GoogleMap mMap) {
        String polyline = results.getOverviewPolyline();
        List<LatLng> decoded = PolyUtil.decode(polyline);
        mMap.addPolyline(new PolylineOptions().addAll(decoded).color(R.color.polylinecolor));
    }




}
