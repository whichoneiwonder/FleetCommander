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

    public Player(int id, int turn, int maxSteps, ArrayList<DefaultShip> fleet) {
        this.id = id;
        this.turn = turn;
        this.maxSteps = maxSteps;
        this.fleet = fleet;
    }

    public void updatePlayer(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);

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
            ArrayList<Integer> directionList = new ArrayList<Integer>();

            if (directionsJSON != null) {
                int len = directionsJSON.length();
                for (int j = 0;i<len;i++){
                    directionList.add(directionsJSON.getInt(j));
                }
            }

            ship.setDirectionList(directionList);
        }
    }

    public String toJSONString() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("turn", turn);
        data.put("maxSteps", maxSteps);

        JSONArray ships = new JSONArray();
        for (DefaultShip ship: fleet) {
            JSONObject shipJSON = new JSONObject();
            shipJSON.put("health", ship.getHealth());

            Location loc = ship.getLoc();
            JSONObject locJSON = new JSONObject();
            locJSON.put("x", loc.getX());
            locJSON.put("y", loc.getY());
            shipJSON.put("loc", locJSON);

            shipJSON.put("dir", ship.getDir());
            ArrayList<Integer> dirList = ship.getDirectionList();
            JSONArray dirJSON = new JSONArray();
            for (int one_dir: dirList) {
                dirJSON.put(one_dir);
            }
            shipJSON.put("dir_list", dirJSON);
            ships.put(shipJSON);
        }

        data.put("ships", ships);

        return data.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public ArrayList<DefaultShip> getFleet() {
        return fleet;
    }

    public void setFleet(ArrayList<DefaultShip> fleet) {
        this.fleet = fleet;
    }
}
