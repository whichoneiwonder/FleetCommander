package com.project.jaja.fleetcommander.code;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 */
public class DefaultShip implements Ship {
    private Location loc;
    private int dir;
    private int health;
    private ArrayList<Integer> directionList;

    public DefaultShip(Location loc, int dir, int health, ArrayList<Integer> directionList) {
        this.loc = loc;
        this.dir = dir;
        this.health = health;
        this.directionList = directionList;
    }

    public Location getLoc() {
        return loc;
    }

    public int getDir() {
        return dir;
    }

    public int getHealth() {
        return health;
    }

    public ArrayList<Integer> getDirectionList() {
        return directionList;
    }

    public void setDirectionList(ArrayList<Integer> directionList) {
        this.directionList = directionList;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
