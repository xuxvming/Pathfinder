

package com.group12.p2p;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

;


/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */

public class FileReceiveService extends IntentService {



    public FileReceiveService(String name) {
        super(name);
    }

    public FileReceiveService() {
        super("FileServerAsyncTask");
    }
    private String RTPIFILE = "rtpidata";


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Context context = getApplicationContext();
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d(WifiDirectService.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(WifiDirectService.TAG, "Server: connection done");


            try (FileOutputStream fos = getApplicationContext().openFileOutput(RTPIFILE, Context.MODE_PRIVATE)) {
                Log.d(WifiDirectService.TAG, "server: copying files !!!!!!!!!!!!!!!!!!!!!!!");
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, fos);
                serverSocket.close();

            } catch (FileNotFoundException e) {
                Log.d(WifiDirectService.TAG , "Write file FileNotFoundException: " + e);
            }
        } catch (IOException e) {
            Log.e(WifiDirectService.TAG, e.getMessage());
        }

        // Success, callback

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
