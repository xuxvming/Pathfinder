package com.group12.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionChecker {

    public static boolean checkPermission(Activity activity,String permission){
        int result = ContextCompat.checkSelfPermission(activity, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, final int code,String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
            Toast.makeText(activity,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{permission},code);
        }
    }
}
