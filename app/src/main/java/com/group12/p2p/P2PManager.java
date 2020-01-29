package com.group12.p2p;

import android.content.Context;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.discovery.NearDiscovery;
import com.adroitandroid.near.model.Host;

import java.util.Set;

public class P2PManager {
    private static final long TIMEOUT = 0;


    private NearDiscovery getNearDiscovery(Context context) {
        NearDiscovery mNearDiscovery = new NearDiscovery.Builder().
                setContext(context).setDiscoveryListener(null, Looper.getMainLooper())
                .build();
        return mNearDiscovery;
    }

    public NearConnect connect(NearDiscovery discovery, Context context) {
        NearConnect mNearConnect = new NearConnect.Builder().setContext(context).build();
        return mNearConnect;
    }

    @NonNull
    private NearDiscovery.Listener getNearDiscoveryListener() {
        return new NearDiscovery.Listener() {
            @Override
            public void onPeersUpdate(Set<Host> hosts) {
                for (Host host: hosts){
                        //cache host object
                }
            }

            @Override
            public void onDiscoveryTimeout() {

            }

            @Override
            public void onDiscoveryFailure(Throwable e) {
               //Toast
            }

            @Override
            public void onDiscoverableTimeout() {
                //restart discoverable
            }
        };
    }

    /**
    * Assume message in JSON format
     * {lat: 1234,
     * lng: 1234,
     * stopname: O'connel Street}*/
    @NonNull
    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, final Host sender) {
                // new AbstractTransportResponse
            }

            @Override
            public void onSendComplete(long jobId) {
              //Log
            }

            @Override
            public void onSendFailure(Throwable e, long jobId) {

            }

            @Override
            public void onStartListenFailure(Throwable e) {
                // This tells that the NearConnect.startReceiving() didn't go through properly.
                // Common cause would be that another instance of NearConnect is already listening and it's NearConnect.stopReceiving() needs to be called first
            }
        };
    }

}
