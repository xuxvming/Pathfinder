package com.group12.transport;

import android.os.AsyncTask;
import com.group12.utils.RequestMaker;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestTramStops {

    @Test(expected = IllegalStateException.class)
    public void testGetTramStops() throws ExecutionException, InterruptedException {
        RequestMaker requestMaker = mock(RequestMaker.class);
        AsyncTask task = mock(AsyncTask.class);
        String response = "";
        when(requestMaker.execute(anyString())).thenReturn(task);
        when(task.get()).thenReturn(response);
        TramStops.getTramStops(requestMaker);
    }
}
