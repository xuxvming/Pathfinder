
package com.group12.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WifiDirectService service;
    private Context mapContext;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param service service associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiDirectService service, Context context) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.service = service;
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

                        if (info.groupFormed && info.isGroupOwner) {
                            Intent serviceIntent = new Intent(mapContext, FileReceiveService.class);
                            mapContext.startService(serviceIntent);
                        } else if(info.groupFormed){
                            Log.d(WifiDirectService.TAG, "Starting Send File");
                            Intent serviceIntent = new Intent(mapContext, FileTransferService.class);
                            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
                            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                            mapContext.startService(serviceIntent);
                        }

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
