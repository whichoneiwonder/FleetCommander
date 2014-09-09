package com.project.jaja.fleetcommander.code;

/**
 * Created by anty on 5/09/14.
 */
public class GameObject{
    private Location loc;
    private int dir;
    private int health;
    private int[] directionList;

    public GameObject(Location loc, int dir, int maxSteps, int health, int[] directionList) {
        this.loc = loc;
        this.dir = dir;
        this.health = health;
    }

    public GameObject(){

    }

    //TODO implement these methods

    public boolean detectCollision(){
        return true;
    }

    public boolean render(){
        return true;
    }

    public boolean destroy(){
        return true;
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

    public int[] getDirectionList() {
        return directionList;
    }

    public void setDirectionList(int[] directionList) {
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
