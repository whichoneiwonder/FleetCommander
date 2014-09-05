package com.project.jaja.fleetcommander.code;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 */
public class Player {
    private int id;

    // Will implement if we have time
    // private Statistic stats;
    private int turn = 0;
    private int maxSteps;
    private ArrayList<DefaultShip> fleet;

    public void updatePlayer(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);
        int[] directionList = new int[maxSteps];

        if (data.getInt("maxSteps") != maxSteps) {
            System.out.println("maxSteps changed, system exiting...");
            System.exit(1);
        }

        // Checks if correct player id has been given
        if (data.getInt("id") != id) {
            System.out.println("player id changed, system exiting...");
            System.exit(1);
        }

        // Checks if the correct turn has been given
        Integer newTurn = data.getInt("turn");
        if (newTurn != turn + 1) {
            System.out.println("Turn " + newTurn.toString() + " has been given, instead of " +
                    Integer.toString(turn + 1) + ". System exiting...");
            System.exit(1);
        } else {
            turn = newTurn;
        }

        JSONArray ships = data.getJSONArray("ships");

        // Make sure no extra ships are added in
        int maxShips = Math.min(ships.length(), 3);

        for (int i = 0; i < maxShips; i++) {
            JSONObject shipJSON = ships.getJSONObject(i);
            JSONObject location = shipJSON.getJSONObject("loc");

            DefaultShip ship = fleet.get(i);
            Location loc = new Location(location.getInt("x"), location.getInt("y"));
            ship.setLoc(loc);
            ship.setDir(shipJSON.getInt("dir"));
            ship.setHealth(shipJSON.getInt("health"));

            JSONArray directionsJSON = shipJSON.getJSONArray("dir_list");

            // Make sure no extra moves are added in
            int maxMoves = Math.min(maxSteps, directionsJSON.length());
            for (int j = 0; j < maxMoves; j++) {
                directionList[j] = directionsJSON.getInt(j);
            }

            ship.setDirectionList(directionList);
        }
    }
}
