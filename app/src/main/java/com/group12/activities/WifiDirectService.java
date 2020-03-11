package com.group12.activities;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.core.content.FileProvider;


import com.group12.p2p.WiFiDirectBroadcastReceiver;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiDirectService extends IntentService implements WifiP2pManager.ChannelListener {
    public static final String TAG = "WifiServiceDiscovery";
    public static final String SERVICE_INSTANCE = "wayfinder";
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private Context mapContext;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private final IBinder myBinder = new MyLocalBinder();


    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


    public WifiDirectService() { super("WifiDirectService"); }



    @Override
    protected void onHandleIntent(Intent intent) {

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void resetData() {
        //To do
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */


    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }


    public void initialiseWifiService(Context context, MapsActivity activity){
        // add necessary intent values to be matched.
        mapContext = context.getApplicationContext();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, mapContext, activity);
        registerReceiver(receiver, intentFilter);
        startRegistration();
    }

    public class MyLocalBinder extends Binder {
        WifiDirectService getService() {
            return WifiDirectService.this;
        }

    }
    private void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WifiDirectService.this,"Added Local Service",Toast.LENGTH_LONG);

            }
            @Override
            public void onFailure(int error) {
                Toast.makeText(WifiDirectService.this,"Failed to add a service",Toast.LENGTH_LONG);

            }
        });
    }

    public WifiP2pManager getManager(){
        return manager;
    }


    public void startDiscovery(){
        // After attaching listeners, create a service request and initiate
        // discovery.

        manager.setDnsSdResponseListeners(channel, new DnsSdServiceResponseListener() {

            @Override
            public void onDnsSdServiceAvailable(String instanceName,
                                                String registrationType, WifiP2pDevice srcDevice) {
                if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                    Log.d(TAG, "Connecting");
                    connect(srcDevice);
                    Log.d(TAG, "wayfinderServiceAvailable " + instanceName);
                }
            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {
            /**
             * A new TXT record is available. Pick up the advertised
             * buddy name.
             */
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomainName, Map<String, String> record,
                    WifiP2pDevice device) {
                Log.d(TAG,
                        device.deviceName + " is "
                                + record.get(TXTRECORD_PROP_AVAILABLE));
            }
        });
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Added service discovery request");
                    }
                    @Override
                    public void onFailure(int arg0) {
                        Log.d(TAG,"Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"Added service discovery request");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"Added service discovery request failed "+reason);

            }
        });
    }


    public void connect(WifiP2pDevice device){

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Connect successful.");
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry.");
            }
        });

    }



    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }




    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */



}
