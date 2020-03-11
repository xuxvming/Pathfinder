
package com.group12.p2p;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
//import com.group12.activities.WiFiDirectActivity;

import com.group12.activities.WifiDirectService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 20000;
    public static final String ACTION_SEND_FILE = "com.group12.p2p.SEND_FILE";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(WifiDirectService.TAG, "Starting File Transfer Intent");
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            Log.d(WifiDirectService.TAG, "Action Send File ");
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(WifiDirectService.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(WifiDirectService.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                InputStream is = null;
                try {
                    is = getAssets().open("test.txt");
                } catch (FileNotFoundException e) {
                    Log.d(WifiDirectService.TAG, e.toString());
                }
                copyFile(is, stream);
                Log.d(WifiDirectService.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(WifiDirectService.TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WifiDirectService.TAG, e.toString());
            return false;
        }
        return true;
    }
}
