package com.group12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class RealtimeDataActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener  {
    private Context mContext = RealtimeDataActivity.this;
    private static final int REQUEST = 112;
    private boolean hasData;
    private ArrayList<String[]> railData;
    private ArrayList<String[]> busData;
    private ArrayList<String[]> luasData;
    private MaterialSearchBar searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_data);
        searchBar = findViewById(R.id.stop_search_bar);
        searchBar.setOnSearchActionListener(this);

        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }  // Permission has already been granted

        if (!updateRTPIData()){
            // No internet connection
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rtpi_action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.p2p_update:
                // Do nothing for now


                return true;

            case R.id.network_update:

                updateRTPIData();


                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    private boolean updateRTPIData(){

        if (isNetworkConnected()){

            new Thread(new Runnable() {

                @Override
                public void run() {


                    railData = makeCSV("rail");
                    luasData = makeCSV("luas");
                    busData = makeCSV("bus");

                    Log.d("RTPI ACTIVITY", "updated rtpi");

                    RealtimeDataActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            hasData = true;

                            searchBar.setPlaceHolder("Enter a stop Id");
                            searchBar.setEnabled(true);

                        }
                    });
                }
            }).start();

            searchBar.setPlaceHolder("Updating...");
            searchBar.setEnabled(false);
            return true; // Was able so send update request
        }
        else {
            hasData = false;
            searchBar.setPlaceHolder("No Data");
            searchBar.setEnabled(false);

            Toast.makeText(getApplicationContext(), "Not Connected Could not Update RTPI", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSearchConfirmed(CharSequence text) {

        if (!hasData) {
            Toast.makeText(getApplicationContext(), "No RTPI data available", Toast.LENGTH_SHORT).show();
            return;
        }

        String stop = text.toString();
        String[] tempB, tempL, tempR;
        TextView tvR = findViewById(R.id.tvRoute);
        TextView tvDst = findViewById(R.id.tvDestination);
        TextView tvD = findViewById(R.id.tvDue);
        tvR.setText("");
        tvD.setText("");
        tvDst.setText("");
        tvR.append("Route\n");
        tvD.append("Due\n");
        tvDst.append("Destination\n");
        for (int i = 0; i < busData.size(); i++) {
            tempB = busData.get(i);
            if (tempB[18].equals(stop)) {
                tvR.append("\n" + tempB[15]);
                tvD.append("\n" + tempB[1]);
                tvDst.append("\n" + tempB[6]);
            }
        }
        for (int i = 0; i < luasData.size(); i++) {
            tempL = luasData.get(i);
            if (tempL[18].equals(stop)) {
                tvR.append("\n" + tempL[15]);
                tvDst.append("\n" + tempL[6]);
                tvD.append("\n" + tempL[1]);
            }
        }
        for (int i = 0; i < railData.size(); i++) {
            tempR = railData.get(i);
            if (tempR[18].equals(stop)) {
                tvR.append("\n" + tempR[15]);
                tvDst.append("\n" + tempR[6]);
                tvD.append("\n" + tempR[1]);
            }
        }
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(mContext, "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public ArrayList<String[]> makeCSV(final String urlHost){
        final ArrayList<String> urls = new ArrayList<>();
        final ArrayList<String[]> listStringArray = new ArrayList<>();
        try {
            // Create a URL for the desired page
            URL url = new URL("http://35.202.105.121/" + urlHost);
            //First open the connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String str;
            String[] stringArray;

            while ((str = in.readLine()) != null) {
                urls.add(str);
                stringArray = str.split(",");
                listStringArray.add(stringArray);
            }
            in.close();
        } catch (Exception e) {
            Log.d("Thread CSV", e.toString());
        }
        //since we are in background thread, to post results we have to go back to ui thread. do the following for that
        RealtimeDataActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                writeCSV(urls, urlHost);
            }
        });
        return listStringArray;
    }

    public static void writeCSV(ArrayList<String> urls, String urlHost){
        String filename = urlHost + ".csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File logDir = new File(directoryDownload, "androidMaps"); //Creates a new folder in DOWNLOAD directory
        logDir.mkdirs();
        File file = new File(logDir, filename);
        if(file.exists())
            file.delete();
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file, true);
            for (int i = 0; i < urls.size(); i += 3) {
                outputStream.write((urls.get(i)+ "\n").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
};
