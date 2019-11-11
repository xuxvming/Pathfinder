package com.group12.pathfinder;

import android.content.Context;
import com.group12.main.MapsActivity;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PathFinderFactoryTest {

    private MapsActivity mapsActivity;

    @Before
    public void setup(){
        mapsActivity = mock(MapsActivity.class);
    }

    @Test
    public void testGetAPIKey(){

    }
}
