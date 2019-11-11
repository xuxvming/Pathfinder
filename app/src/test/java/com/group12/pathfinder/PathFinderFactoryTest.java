package com.group12.pathfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import com.group12.main.MapsActivity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PathFinderFactoryTest {

    private MapsActivity mapsActivity;
    private PathFinderFactory factory;
    private Context context;

    @Before
    public void setup(){
        mapsActivity = mock(MapsActivity.class);
        context = mock(Context.class);
        when(mapsActivity.getApplicationContext()).thenReturn(context);
        factory = new PathFinderFactory(mapsActivity.getApplicationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testGetAPIKey() throws PackageManager.NameNotFoundException {
        ApplicationInfo info = mock(ApplicationInfo.class);
        String packageName = "com.group12.androidmapsservice";
        PackageManager packageManager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn(packageName);
        when(packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA)).thenReturn(info);
        factory.getApiKey();
    }
}
