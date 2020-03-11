

package com.group12.p2p;

import android.app.Fragment;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.group12.activities.R;
import com.group12.activities.WifiDirectService;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;


/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */

public class FileServerAsyncTask extends IntentService {



    public FileServerAsyncTask(String name) {
        super(name);
    }

    public FileServerAsyncTask() {
        super("FileServerAsyncTask");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Context context = getApplicationContext();
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d(WifiDirectService.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(WifiDirectService.TAG, "Server: connection done");
            final File f = new File(context.getExternalFilesDir("received"), "received.txt");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.d(WifiDirectService.TAG, "server: copying files " + f.toString());
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
        } catch (IOException e) {
            Log.e(WifiDirectService.TAG, e.getMessage());
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
