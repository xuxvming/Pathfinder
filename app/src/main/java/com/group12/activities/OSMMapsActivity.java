package com.group12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group12.p2p.WifiDirectService;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.AbstractPathFinder;
import com.group12.pathfinder.OnlinePathFinder;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.utils.PermissionChecker;
import com.group12.utils.RequestMaker;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class OSMMapsActivity extends Activity implements LocationListener {
    public static final String TAG = "OSMMapsActivity";
    private FloatingActionButton searchButton;
    private ProgressBar spinner;
    MapView map = null;
    GeoPoint current_position;
    String current_available_modes;
    public LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private IMapController mapController;
    private WifiDirectService wifiDirectService;
    private PathFinderFactory pathFinderFactory = new PathFinderFactory();
    boolean isBound = false;
    private boolean centreMap = true;
    private AbstractDirectionsObject response;
    private MapEventsOverlay OverlayGesture;

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            WifiDirectService.MyLocalBinder binder = (WifiDirectService.MyLocalBinder) service;
            wifiDirectService = binder.getService();
            isBound = true;
            setupWifiDirectService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public void setupWifiDirectService() {
        WifiP2pManager manager = wifiDirectService.initialiseWifiService(this);
        if (manager == null) {
            Log.d(TAG, "Houston we have a problem");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_osm_maps);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        FloatingActionButton locationButton = findViewById(R.id.location_button);
        searchButton = findViewById(R.id.search_button);
        final FloatingActionButton p2pButton = findViewById(R.id.p2p);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(9.5);
        MapEventsReceiver mReceive = new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.d(TAG, "Single tap helper");
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.d(TAG, "LongPressHelper");
                String longitude = Double.toString(p.getLongitude());
                String latitude = Double.toString(p.getLatitude());
                Log.d(TAG, "Gesture Longitude: " + longitude + " Latitude: " + latitude);
                pathFinderFactory.setSearchText(latitude+","+longitude);
                Intent intent = new Intent(OSMMapsActivity.this,SearchActivity.class);
                intent.putExtra(PathFinderFactory.class.getName(),pathFinderFactory);
                startActivity(intent);
                return false;
            }

        };
        //Creating a handle overlay to capture the gestures
        OverlayGesture = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayGesture);

        //Refreshing the map to draw the new overlay
        map.invalidate();
        getLocation();
        response = (AbstractDirectionsObject) getIntent().getSerializableExtra("Response");
        if (response != null){
            Map<String, List<Polyline>> lines = getPolylines(response);
            for (Map.Entry<String,List<Polyline>> entry:lines.entrySet()){
//                    line.getOutlinePaint().setColor(getColor(entry.getKey()));
                    map.getOverlays().addAll(entry.getValue());
            }
            GeoPoint origin = response.getStartPoint();
            GeoPoint destination = response.getEndPoint();
            Marker startMarker = new Marker(map);
            startMarker.setTextIcon("start: " + origin.toDoubleString());
            startMarker.setPosition(origin);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);

            Marker endMarker = new Marker(map);
            endMarker.setTextIcon("end: " + destination.toDoubleString());
            endMarker.setPosition(destination);
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(endMarker);
        }
        Intent intent = new Intent(this, WifiDirectService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        p2pButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiDirectService.isSearching = true;
                wifiDirectService.startDiscovery();
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //getLocation();
                                                Intent intent = new Intent(OSMMapsActivity.this,SearchActivity.class);
                                                intent.putExtra(PathFinderFactory.class.getName(),pathFinderFactory);
                                                startActivity(intent);
                                            }
                                        }
        );
        locationButton.setOnClickListener(new OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                centreMap = true;
                getLocation();
            }

        });

    }

    public void onResume() {
        super.onResume();
        map.onResume();
        setLocationUpdates();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
        locationManager.removeUpdates(this);
    }

    protected void getLocation() {
        Log.d(TAG, "Getting Location");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setLocationUpdates();
    }

    public void setLocationUpdates(){
        int minTime;
        int minDistance;
        if (centreMap){
            minTime = 0;
            minDistance = 0;
        } else{
            minTime = 60000;
            minDistance = 100;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting Location Permissions");
            PermissionChecker.requestPermission(OSMMapsActivity.this,MY_PERMISSIONS_REQUEST_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        }
    }


    public void onLocationChanged(Location location) {
        current_position = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (centreMap) {
            mapController.setCenter(current_position);
            centreMap = false;
            Marker startMarker = new Marker(map);
            startMarker.setTextIcon("start: " + current_position.toDoubleString());
            startMarker.setPosition(current_position);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
            map.invalidate();
        }
        else if (response != null) {
            ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork.isConnected()) {
                checkForTransportUpdates();
            }
        }
        setLocationUpdates();
        pathFinderFactory.setOriginLatLng(current_position);
        Log.d(TAG, "latitude:" + location.getLatitude() + " longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        getLocation();
    }

    private Map<String,List<Polyline>> getPolylines(AbstractDirectionsObject object){
        Map<String, List<Polyline>> res = new HashMap();
        Set<String> availableRoutes = object.getAvailableRoutes();
        for (String route:availableRoutes){
            Map<String, List<List<GeoPoint>>> tempMap = object.getAvailableRoute(route);
            for (String mode : tempMap.keySet()){
                for (List<GeoPoint> coordinates: tempMap.get(mode)){
                    Polyline line = new Polyline();
                    line.getOutlinePaint().setColor(getColor(mode));
                    line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
                    line.setGeodesic(true);
                    line.setPoints(coordinates);
                    if (res.containsKey(mode)){
                        res.get(mode).add(line);
                    }else{
                        List<Polyline> list = new LinkedList<>();
                        list.add(line);
                        res.put(mode,list);
                    }
                }
            }
        }
        return res;
    }

    private int getColor(String mode){
        switch (mode) {
            case "drive":
                return getResources().getColor(R.color.polylinecolor_drive);
            case "walk":
                return getResources().getColor(R.color.polylinecolor_walk);
            case "bus":
                return getResources().getColor(R.color.polylinecolor_bus);
            default:
                return getResources().getColor(R.color.polylinecolor_luas);
        }
    }

    private void checkForTransportUpdates() {
        Log.i(TAG,"Checking for Transport Status Updates");
        HttpGetRequest getRequest = new HttpGetRequest();
        getRequest.execute();

    }

    private void removePolyLines(){
        map.getOverlays().clear();
        map.getOverlays().add(OverlayGesture);
    }
    public class HttpGetRequest extends AsyncTask<AbstractPathFinder, Void, String> {
        private final Logger LOGGER = LoggerFactory.getLogger(OSMMapsActivity.class);
        private String requestMethod = "GET";
        @Override
        protected String doInBackground(final AbstractPathFinder... params){

            try{
                URL url = new URL("http://35.202.105.121/status");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(requestMethod);
                InputStream inputStream = connection.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) !=null){
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException e) {
                LOGGER.error("Error making request" ,e );
            }
            return null;

        }
        @Override
        protected void onPostExecute(String result){
            Log.i(TAG,"PostExecute Activated");
            if (result == null && result.isEmpty()){
                return;
            }
            if (current_available_modes == null){
                current_available_modes = result;
                return;
            }
            if (!result.equals(current_available_modes)){
                spinner.setVisibility(View.VISIBLE);
                current_available_modes = result;
                removePolyLines();
                Log.i(TAG,"Change detected");
                AbstractPathFinder pathFinder = new OnlinePathFinder(current_position, response.getEndPoint(), PathFinderFactory.getWebServiceApi(), response.getTravelChoice());
                response = pathFinder.makeRequest(new RequestMaker());

                Map<String, List<Polyline>> lines = getPolylines(response);
                for (Map.Entry<String,List<Polyline>> entry:lines.entrySet()){
                    map.getOverlays().addAll(entry.getValue());
                }
                map.invalidate();
                spinner.setVisibility(View.GONE);
            }
        }
    }
}



