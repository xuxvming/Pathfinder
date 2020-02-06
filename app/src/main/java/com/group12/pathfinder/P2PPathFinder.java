package com.group12.pathfinder;

import com.group12.transport.AbstractTransportResponse;

public class P2PPathFinder extends AbstractPathFinder {
    P2PPathFinder(String origin, String destination, String url) {
        super(origin, destination, url);
    }

    @Override
    public String createURl() {
        return null;
    }
}
