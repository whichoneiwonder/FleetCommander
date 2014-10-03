package com.project.jaja.fleetcommander.code;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultShip1Test extends TestCase {

    public void testShip() throws Exception {
        Location location = new Location(1,1);
        ArrayList<Integer> dirs1 = new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,0,1,2)){};
        DefaultShip ship = new DefaultShip(location, 0, 100, dirs1);
        assertEquals(dirs1, ship.getDirectionList());
    }
}