package com.project.jaja.fleetcommander;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 * This class looks after the Player class and details
 */
public class Player {
    // This Player's IP address
    private String ip;

    // This Player's MAC address
    private String macAddress;

    // Current turn number, used for fraud detection when updating opponent player
    private int turn = 0;

    // The maximum amount of steps a Player can make with each DefaultSHip
    private int maxSteps;

    // The fleet of Ships
    private ArrayList<Ship> fleet;

    //Keeping track of which ship colour the player has
    private String shipColour;

    /**
     * Constructs a Player with an IP address, MAC Address, turn, maxSteps and an array of ships -
     * a fleet and a ship colour
     * @param ip IP Address
     * @param macAddress MAC Address
     * @param turn Turn number
     * @param maxSteps Maximum steps taken per turn
     * @param shipColour Colour of the Ship
     */
    public Player(String ip, String macAddress, int turn, int maxSteps, String shipColour) {
        this.ip = ip;
        this.macAddress = macAddress;
        this.turn = turn;
        this.maxSteps = maxSteps;
        this.shipColour = shipColour;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setShipColour(String shipColour) {
        this.shipColour = shipColour;
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

    public ArrayList<Ship> getFleet() {
        return fleet;
    }

    public void setFleet(ArrayList<Ship> fleet) {
        this.fleet = fleet;
    }

    public void removeShipFromFleet(Ship deadShip){
        this.fleet.remove(deadShip);
    }

    public void updatePlayer(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);

        if (data.getInt("maxSteps") != maxSteps) {
            Log.d("playercheck", "maxSteps changed, system exiting...");
            System.exit(1);
        }

        // Checks if correct player IP address has been given
        if (!data.getString("ip").equals(ip)) {
            Log.d("playercheck", "player IP address changed, system exiting...");
            System.exit(1);
        }

        String mac = data.getString("mac");
        if (!(mac.equals(macAddress) || mac.equals(""))) {
            Log.d("playercheck", "player MAC address changed, system exiting...");
            System.exit(1);
        }

        // Checks if the correct turn has been given
        Integer newTurn = data.getInt("turn");
        if (newTurn != turn + 1) {
            Log.d("playercheck", "Turn " + newTurn.toString() + " has been given, instead of " +
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
            JSONObject path = shipJSON.getJSONObject("path");

            Ship ship = fleet.get(i);

            //Removing this for now, seeing if we can get the ship to follow the path
            //that it is passed
            //Location loc = new Location(location.getInt("x"), location.getInt("y"));
            //ship.setLoc(loc);
            ship.setDir(shipJSON.getInt("dir"));
            ship.setHealth(shipJSON.getInt("health"));

            //Adding the x and y coordinates from the json path arrays to the ships
            JSONArray xCoords = path.getJSONArray("xCoordsPath");
            JSONArray yCoords = path.getJSONArray("yCoordsPath");

            //Both JSON arrays should have the same length as we cannot have an x coord
            //without a y coord
            for(int j = 0; j < xCoords.length(); j++ ){
                ship.addxCoord(xCoords.getInt(j));
                ship.addyCoord(yCoords.getInt(j));
            }


            JSONArray directionsJSON = shipJSON.getJSONArray("dir_list");
            ArrayList<Integer> directionList = new ArrayList<Integer>();

            if (directionsJSON != null) {
                int len = directionsJSON.length();

                for (int j = 0; i < len; i++){
                    directionList.add(directionsJSON.getInt(j));
                }
            }

            ship.setDirectionList(directionList);
        }
    }

    public String toJSONString() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("ip", ip);
        data.put("mac", macAddress);
        data.put("turn", turn);
        data.put("maxSteps", maxSteps);

        JSONArray ships = new JSONArray();
        for (Ship ship: fleet) {
            JSONObject shipJSON = new JSONObject();
            shipJSON.put("health", ship.getHealth());

            Location loc = ship.getLoc();
            JSONObject locJSON = new JSONObject();
            locJSON.put("x", loc.getX());
            locJSON.put("y", loc.getY());
            shipJSON.put("loc", locJSON);

            //This allows us to have reference to the path that
            //the user has drawn for that ship
            JSONObject pathJSON = new JSONObject();
            pathJSON.put("xCoordsPath", ship.getxCoords());
            pathJSON.put("yCoordsPath", ship.getyCoords());
            shipJSON.put("path", pathJSON);

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

    public String getShipColour(){
        return shipColour;
    }

    /*
    A simple method used to determine whether or not the player still has any ships left
    in their fleet. If they do, then they may continue playing. If not, then they lose
     */
    public boolean stillHasShips(){
        return this.getFleet().size() > 0;
    }
}
