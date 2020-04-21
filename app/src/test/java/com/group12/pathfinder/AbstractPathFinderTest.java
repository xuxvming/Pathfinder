package com.group12.pathfinder;

import android.os.AsyncTask;
import com.group12.utils.RequestMaker;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AbstractPathFinderTest {

    @Test
    public void testDecorder() throws IOException, ExecutionException, InterruptedException {
        RequestMaker requestMaker = mock(RequestMaker.class);
        FileReader reader = new FileReader(new File("src/test/resouces/result.json"));
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
        OnlinePathFinder onlinePathFinder = new OnlinePathFinder(new GeoPoint(0.0,0.0),new GeoPoint(0.0,0.0),"",1);
        AbstractDirectionsObject abstractDirectionsObject = onlinePathFinder.makeRequest(requestMaker);
        assertNotNull(abstractDirectionsObject.getAvailableRoutes());
        assertNotNull(abstractDirectionsObject.getModes());
        assertNotNull(abstractDirectionsObject.getModes());
    }
}
