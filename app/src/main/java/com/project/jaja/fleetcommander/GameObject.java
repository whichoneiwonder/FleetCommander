package com.project.jaja.fleetcommander;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 */
public class GameObject{
    private Location loc;
    private int dir;
    private int health;
    private ArrayList<Integer> directionList;

    public GameObject(Location loc, int dir, int maxSteps, int health) {
        this.loc = loc;
        this.dir = dir;
        this.health = health;
    }

    public GameObject(){

    }

    /**
     * A helper method that we can use to determine whether or not the object should
     * be persisted in the game view
     * @return a boolean of whether or not the object is still alive
     */
    public boolean stillAlive(){
        if(this.health <= 0){
            return false;
        } else{
            return true;
        }
    }

    //TODO implement these methods

    public boolean detectCollision(GameObject testObject){
        int playerX = this.loc.getX();
        int playerY = this.loc.getY();

        int enemyX = testObject.getLoc().getX();
        int enemyY  = testObject.getLoc().getY();

        //Going back to high school maths to calculate the distance between two points
        double distance = Math.sqrt(Math.pow(playerX - enemyX, 2) + Math.pow(playerY - enemyY, 2));


        if(distance > 100){ //This is an arbitrary value that will most likely be changed
            //Based on the sprite widths
            return false;
        } else {

            //Damage dealing logic

            return true;
        }
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
