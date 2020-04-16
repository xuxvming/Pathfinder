

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

            //Here we need to check the first few bytes from the inpute stream
            //we should make these be a timestamp so that we can compare this new inputstream with
            //the rtpi file that we alread have

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
