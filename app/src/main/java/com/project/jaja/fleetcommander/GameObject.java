package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by anty and James on 5/09/14.
 */
public class GameObject{
    private Location loc;
    private int dir;
    public int health;
    private ArrayList<Integer> directionList;
    protected String color;

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


    /**
     * A method that determines whether this gameobject is colliding with the one being
     * passed in
     * @param testObject the object we are checking for collision
     * @return returns true if collision is occuring
     */
    public boolean detectCollision(GameObject testObject, Vibrator v){

        Log.d("Collision detection", "detectionCollision() is being called");
        int enemyX = testObject.getxPosition();
        int enemyY  = testObject.getyPosition();

        if(gameView.getMappedScreenX(enemyX) == gameView.getMappedScreenX(xPosition) &&
            gameView.getMappedScreenY(enemyY) == gameView.getMappedScreenY(yPosition) ){//Damage dealing logic
            Log.d("Collision detection","Two Ships are colliding" + "enemy " + testObject.getColor() + " myShip " + this.getColor());
            v.vibrate(2000);
            //We deal different amounts of damage based on what type of collision it is
            if(testObject instanceof Ship){
                int enemyDirection = ((Ship) testObject).getDirection();
                int playerDirection = ((Ship) this).getDirection();

                //Case 1: Head on collision
                if(Math.abs(enemyDirection - playerDirection) == 2){
                    this.decreaseHealth(testObject.getHealth());
                    testObject.decreaseHealth(this.getHealth());
                }

                //Case 2: Rear end
                else if(enemyDirection == playerDirection){
                    switch(playerDirection){
                        //Both ships are facing up, object with smaller y does damage
                        case 0:
                            if(yPosition < enemyY){
                                testObject.kill();
                            } else{
                                this.kill();
                            }
                        //Both ships are facing right, smaller x does damage
                        case 1:
                            if(xPosition < enemyX){
                                testObject.kill();
                            } else{
                                this.kill();
                            }
                        //Both ships are facing down, larger y does damage
                        case 2:
                            if(yPosition < enemyY){
                                this.kill();
                            } else{
                                testObject.kill();
                            }
                        //Both ships are facing right, larger x does damage
                        case 3:
                            if(xPosition < enemyX){
                                this.kill();
                            } else {
                                testObject.kill();
                            }
                    }
                }
                //Case 3: One ship must be T-boning the other
                else{
                    switch(playerDirection){
                        //Player is facing up, thus if the enemy y is greater than
                        //the player y we know that the player is t-boning the enemy
                        case 0:
                            if(yPosition < enemyY){
                                testObject.kill();
                            } else{
                                this.kill();
                            }
                        //Player is facing left, we can just reverse the condition above
                        case 1:
                            if(yPosition < enemyY){
                                testObject.kill();
                            } else{
                                this.kill();
                            }
                        //Player is facing down, if enemy y is less than player y then the enemy
                        // takes damage
                        case 2:
                            if(yPosition < enemyY){
                                this.kill();
                            } else{
                                testObject.kill();
                            }
                        //Player is facing right, same as left
                        case 3:
                            if(yPosition < enemyY){
                                testObject.kill();
                            } else{
                                this.kill();
                            }
                    }

                }



            }
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

    public void decreaseHealth(int updateValue){
        this.health -= updateValue;
    }

    public void increaseHealth(int updateValue){
        this.health += updateValue;
    }

    public void kill(){
        this.health = -1;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
