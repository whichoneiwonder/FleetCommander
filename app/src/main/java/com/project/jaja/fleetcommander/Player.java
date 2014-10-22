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
        this.fleet = new ArrayList<Ship>();
    }


    public void updatePlayerFleet(){
        for(int i = 0 ; i < getFleet().size(); i++){
            Log.d("Ship Position", "Fleet Size: " + getFleet().size() + " Ship X " + i + " : " + getFleet().get(i).getXPosition() + " Ship Y "  + i + " : " + getFleet().get(i).getYPosition());
            if(getFleet().get(i).getHealth() <= 0){
                getFleet().remove(i);
                i--;
            }
        }
    }
    /**
     * A method that takes in the JSON sent by the other player and then updates
     * the player's game board with the data
     * @param jsonData the JSON sent over from the other player
     * @throws JSONException
     */
    public void updatePlayer(String jsonData) throws JSONException {

        JSONObject data = new JSONObject(jsonData);

        //If the max steps is not consistent between players then the game
        // has been compromised
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
        // Empty mac address - after first turn
        if (mac.equals("") && turn == 0) {
            macAddress = mac;

            // Check after every other turn
        } else if (!mac.equals(macAddress)) {
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
            JSONObject path = shipJSON.getJSONObject("path");

            //Retrieving the ship in question from the existing fleet
            Ship ship = fleet.get(i);

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

        }
    }

    /*
        Converts all the necessary data about the player into JSON so that it may
        be sent to their opponent for processing
     */
    public String toJSONString() throws JSONException {
        JSONObject data = new JSONObject();
        //The player's IP address
        data.put("ip", ip);
        //The player's MAC address
        data.put("mac", macAddress);
        //Which turn number the player is on, this is used to ensure that the correct data
        //has been recieved
        data.put("turn", turn);
        //The number of steps that the player is allowed to make (ie how far their
        //ship can move in a single turn)
        data.put("maxSteps", maxSteps);

        JSONArray ships = new JSONArray();
        for (Ship ship: fleet) {
            JSONObject shipJSON = new JSONObject();
            shipJSON.put("health", ship.getHealth());

            //This allows us to have reference to the path that
            //the user has drawn for that ship
            JSONObject pathJSON = new JSONObject();
            pathJSON.put("xCoordsPath", ship.getxCoords());
            pathJSON.put("yCoordsPath", ship.getyCoords());
            shipJSON.put("path", pathJSON);
            ships.put(shipJSON);
        }

        data.put("ships", ships);

        return data.toString();
    }

    //=============================================================================================
    //                          ACCESSOR AND MUTATOR METHODS
    //=============================================================================================

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
