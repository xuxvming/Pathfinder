package com.group12.pathfinder;

import android.os.AsyncTask;
import com.group12.utils.RequestMaker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;


public class GoogleMapsPathFinderTest {

    private GoogleMapsPathFinder pathFinder;

    @Before
    public void setup() {
        String origin = "origin";
        String destination = "destination";
        String url = "someurl";
        String apiKey = "somekey";
        pathFinder = new GoogleMapsPathFinder(origin, destination, url, apiKey);
    }

    @Test
    public void testCreateUrl(){
        String expected = "someurlorigin=origin&destination=destination&key=somekey";
        assertEquals(expected,pathFinder.createURl());
    }

    @Test
    public void testMakeRequest_normal() throws Exception {
        RequestMaker requestMaker = mock(RequestMaker.class);
        FileReader reader = new FileReader(new File ("/Users/xiuxuming/IdeaProjects/android-maps-service/app/src/test/resouces/response.json"));
        double originLat = 53.341888;
        double originLng = -6.2530668;
        double destinationLat = 53.3494559;
        double destinationLng = -6.2025631;
        StringBuilder sb  = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        String res = sb.toString();
        AsyncTask task = mock(AsyncTask.class);
        when(requestMaker.execute(anyString())).thenReturn(task);
        when(task.get()).thenReturn(res);
        AbstractDirectionsObject object = pathFinder.makeRequest(requestMaker);
        assertNotNull(object);
        assertEquals(originLat,object.getOriginLat());
        assertEquals(originLng,object.getOriginLng());
        assertEquals(destinationLat,object.getDestinationLat());
        assertEquals(destinationLng,object.getDestinationLng());
        assertNotNull(object.getOverviewPolyline());
    }
}
