//package com.group12.p2p;
//
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.net.wifi.WpsInfo;
//import android.net.wifi.p2p.WifiP2pConfig;
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pInfo;
//import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
//import android.os.AsyncTask;
//import android.os.Bundle;;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//
//import com.group12.activities.MapsActivity;
//import com.group12.activities.R;
//import com.group12.activities.WifiDirectService;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.Objects;
//
//public class ConnectionHandler extends Fragment implements WifiP2pManager.ConnectionInfoListener {
//
//    @Override
//    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
//
//
//        if (info.groupFormed && info.isGroupOwner) {
//            new FileServerAsyncTask(getActivity())
//                    .execute();
//        }
//        // hide the connect button
//    }
//
//
//    /**
//     * Updates the UI with device data
//     *
//     * @param device the device to be displayed
//     */
////    public void showDetails(WifiP2pDevice device) {
////        this.device = device;
////        Objects.requireNonNull(this.getView()).setVisibility(View.VISIBLE);
////        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
////        view.setText(device.deviceAddress);
////        view = (TextView) mContentView.findViewById(R.id.device_info);
////        view.setText(device.toString());
////
////    }
//
//    /**
//     * A simple server socket that accepts connection and writes some data on
//     * the stream.
//     */
//    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
//
//        private Context context;
//
//        /**
//         * @param context
//         */
//        public FileServerAsyncTask(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//                ServerSocket serverSocket = new ServerSocket(8988);
//                Log.d(WifiDirectService.TAG, "Server: Socket opened");
//                Socket client = serverSocket.accept();
//                Log.d(WifiDirectService.TAG, "Server: connection done");
//                final File f = new File(context.getExternalFilesDir("received"),
//                        "wifip2pshared-" + System.currentTimeMillis()
//                                + ".jpg");
//
//                File dirs = new File(f.getParent());
//                if (!dirs.exists())
//                    dirs.mkdirs();
//                f.createNewFile();
//
//                Log.d(WifiDirectService.TAG, "server: copying files " + f.toString());
//                InputStream inputstream = client.getInputStream();
//                copyFile(inputstream, new FileOutputStream(f));
//                serverSocket.close();
//                return f.getAbsolutePath();
//            } catch (IOException e) {
//                Log.e(WifiDirectService.TAG, e.getMessage());
//                return null;
//            }
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
//         */
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//
//                File recvFile = new File(result);
//                Uri fileUri = FileProvider.getUriForFile(
//                        context,
//                        "com.group12.p2p.fileprovider",
//                        recvFile);
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                intent.setDataAndType(fileUri, "image/*");
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                context.startActivity(intent);
//            }
//
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see android.os.AsyncTask#onPreExecute()
//         */
//        @Override
//        protected void onPreExecute() {
//            //
//        }
//
//    }
//
//    public static boolean copyFile(InputStream inputStream, OutputStream out) {
//        byte buf[] = new byte[1024];
//        int len;
//        try {
//            while ((len = inputStream.read(buf)) != -1) {
//                out.write(buf, 0, len);
//
//            }
//            out.close();
//            inputStream.close();
//        } catch (IOException e) {
//            Log.d(WifiDirectService.TAG, e.toString());
//            return false;
//        }
//        return true;
//    }
//
//
//}
