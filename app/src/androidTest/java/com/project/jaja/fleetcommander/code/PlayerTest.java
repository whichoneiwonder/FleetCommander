package com.project.jaja.fleetcommander.code;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testPlayer() throws Exception {
        ArrayList<DefaultShip> ships = new ArrayList<DefaultShip>();

        Location loc1 = new Location(1, 1);
        ArrayList<Integer> dirs1 = new ArrayList<Integer>(Arrays.asList(2, 3, 4, 5, 6, 7, 0, 1, 2)){};
        DefaultShip ship1 = new DefaultShip(loc1, 0, 100, dirs1);
        ships.add(ship1);

        Location loc2 = new Location(1, 3);
        ArrayList<Integer> dirs2 = new ArrayList<Integer>(Arrays.asList(1,3,4,5,6,7,0,1,2)){};
        DefaultShip ship2 = new DefaultShip(loc2, 1, 90, dirs2);
        ships.add(ship2);

        Location loc3 = new Location(1, 5);
        ArrayList<Integer> dirs3 = new ArrayList<Integer>(Arrays.asList(1,3,4,5,6,7,0,4,5)){};
        DefaultShip ship3 = new DefaultShip(loc3, 2, 80, dirs3);
        ships.add(ship3);

        Player test = new Player("10.0.0.23", 0, 10, ships);

        String expected = "{\"id\":\"10.0.0.23\",\"turn\":0,\"maxSteps\":10,\"ships\":[{\"health\":100,\"loc\":{\"x\":1,\"y\":1},\"dir\":0,\"dir_list\":[2,3,4,5,6,7,0,1,2]},{\"health\":90,\"loc\":{\"x\":1,\"y\":3},\"dir\":1,\"dir_list\":[1,3,4,5,6,7,0,1,2]},{\"health\":80,\"loc\":{\"x\":1,\"y\":5},\"dir\":2,\"dir_list\":[1,3,4,5,6,7,0,4,5]}]}";

        assertEquals(test.getIp(), "10.0.0.23");
        assertEquals(test.getTurn(), 0);
        assertEquals(test.getMaxSteps(), 10);
        assertEquals(test.getFleet(), ships);
        assertEquals(expected, test.toJSONString());

        test.updatePlayer("{\"id\":\"10.0.0.23\",\"turn\":1,\"maxSteps\":10,\"ships\":[{\"health\":10,\"loc\":{\"x\":0,\"y\":0},\"dir\":3,\"dir_list\":[0,0,0,0,0,0,0,0,0]},{\"health\":10,\"loc\":{\"x\":0,\"y\":0},\"dir\":4,\"dir_list\":[0,0,0,0,0,0,0,0,0]},{\"health\":10,\"loc\":{\"x\":0,\"y\":0},\"dir\":5,\"dir_list\":[0,0,0,0,0,0,0,0,0]}]}");

        assertEquals(test.getIp(), "10.0.0.23");
        assertEquals(test.getTurn(), 1);
        assertEquals(test.getMaxSteps(), 10);
        DefaultShip ship_test = test.getFleet().get(0);
        assertEquals(ship_test.getDir(), 3);

    }
}