package com.group12.pathfinder;

import org.junit.Before;
import org.junit.Test;

public class GoogleMapsPathFinderTest {

    private GoogleMapsPathFinder pathFinder;

    @Before
    public void setup(){
        String origin = "here";
        String destination = "there";
        String url = "queryURL";
        pathFinder = new GoogleMapsPathFinder(origin, destination, url);
    }

    @Test
    public void testGetApiKey(){

    }
}
