package com.group12.pathfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.group12.activities.MapsActivity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
