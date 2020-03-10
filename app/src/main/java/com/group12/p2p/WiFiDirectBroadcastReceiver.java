
package com.group12.p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.group12.activities.MapsActivity;
import com.group12.activities.R;
import com.group12.activities.WifiDirectService;

import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WifiDirectService service;
    private Context mapContext;
    private MapsActivity activity;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param service service associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiDirectService service, Context context, MapsActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.service = service;
        this.activity = activity;
        this.mapContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                service.setIsWifiP2pEnabled(true);
            } else {
                service.setIsWifiP2pEnabled(false);
                service.resetData();

            }
            Log.d(WifiDirectService.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {


            Log.d(WifiDirectService.TAG, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                        activity.setInfo(info);
                        if (info.groupFormed && info.isGroupOwner) {
                            new WifiDirectService.FileServerAsyncTask(mapContext).execute();

                        } else if(info.groupFormed){
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            activity.startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);

                        }
                           //  hide the connect button
                        }

                });
                Log.d(WifiDirectService.TAG, "Network info is connected");
            } else {
                Log.d(WifiDirectService.TAG, "Network info is not connected");

            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(WifiDirectService.TAG, "P2P this device changed action");

        }
    }


}
