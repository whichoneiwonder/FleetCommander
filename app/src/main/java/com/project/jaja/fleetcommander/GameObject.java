package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by anty and James on 5/09/14.
 */
public class GameObject{
    private Location loc;
    private int dir;
    private int health;
    private ArrayList<Integer> directionList;

    //the bitmap image of the sprite itself:
    public Bitmap map;

    //the View the sprite will be rendered into
    public GameView gameView;

    // Speed X and Y values
    public int xSpeed;
    public int ySpeed;

    //position X and Y Values
    public int xPosition;

    public int getyPosition() {
        return yPosition;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int yPosition;

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


        int enemyX = testObject.getxPosition();
        int enemyY  = testObject.getyPosition();



        if(enemyX > xPosition && enemyX < xPosition + map.getWidth()
                && enemyY > yPosition && enemyY < yPosition + map.getHeight()){
            //Damage dealing logic
            Log.d("Collision detection","This is being reached");
            return true;
        } else {
            return false;
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
