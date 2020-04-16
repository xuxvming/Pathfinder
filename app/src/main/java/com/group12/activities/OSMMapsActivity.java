package com.group12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group12.p2p.WifiDirectService;
import com.group12.pathfinder.AbstractDirectionsObject;
import com.group12.pathfinder.Coordinates;
import com.group12.pathfinder.P2PPathFinder;
import com.group12.pathfinder.PathFinderFactory;
import com.group12.utils.PermissionChecker;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;


public class OSMMapsActivity extends Activity implements LocationListener {
    public static final String TAG = "OSMMapsActivity";
    private FloatingActionButton locationButton;
    private FloatingActionButton searchButton;
    MapView map = null;
    public LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private IMapController mapController;
    private WifiDirectService wifiDirectService;
    private PathFinderFactory pathFinderFactory = new PathFinderFactory();
    boolean isBound = false;

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
        locationButton = findViewById(R.id.location_button);
        searchButton = findViewById(R.id.search_button);
        final FloatingActionButton p2pButton = findViewById(R.id.p2p);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(9.5);
        AbstractDirectionsObject response = (AbstractDirectionsObject) getIntent().getSerializableExtra("Response");
        if (response != null){
            Polyline line = addPolyline(response);
            map.getOverlays().add(line);
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
                                                getLocation();
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
                getLocation();
            }

        });
        getLocation();
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
        locationManager.removeUpdates(this);
    }

    protected void getLocation() {
        Log.d(TAG, "Getting Location");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting Location Permissions");
            PermissionChecker.requestPermission(OSMMapsActivity.this,MY_PERMISSIONS_REQUEST_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);

            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }


    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        GeoPoint current_position = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(current_position);
        pathFinderFactory.setOriginLatLng(current_position);
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

    private Polyline addPolyline(AbstractDirectionsObject object){
        Polyline line = new Polyline();
        if (object instanceof Coordinates){
            for (GeoPoint point:((Coordinates) object).getCoordiantelist()){
                line.addPoint(point);
            }
        }
        return line;
    }


}


