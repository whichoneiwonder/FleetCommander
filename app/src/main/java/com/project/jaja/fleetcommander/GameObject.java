package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by anty and James on 5/09/14.
 * This class represents the fundamental behaviour of an object in the game.
 * While it has only been used to represent a ship, had our stretch goal of animated
 * cannonballs been reached then it would have been used for that as well
 */
public class GameObject{

    //The GameObjects current location
    private Location loc;

    //The direction that the game object is facing
    private int dir;

    //The GameObjects current health (0 represents dead)
    public int health;

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
    public int yPosition;


    /**
     * The standard constuctor for a gameObject
     * @param loc The location at which the object will first be created
     * @param dir The direction that the created object will be facing
     * @param maxSteps The maximum number of grid spaces this object can traverse in a single
     *                 turn
     * @param health The starting health of the gameObject
     */
    public GameObject(Location loc, int dir, int maxSteps, int health) {
        this.loc = loc;
        this.dir = dir;
        this.health = health;
    }

    /**
     * The default constructor for a GameObject
     */
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
     * A method that determines whether this gameObject is colliding with the one being
     * passed in
     * @param testObject the object we are checking for collision
     * @param v A reference to the android vibrator. This allows us to provide the player with
     *          haptic feedback when collision occurs
     * @return returns true if collision is occurring
     */
    public boolean detectCollision(GameObject testObject, Vibrator v){

        //We first need to determine the X and Y positions of the testObject
        int enemyX = testObject.getxPosition();
        int enemyY  = testObject.getyPosition();


        //From these X and Y coordinates we then need to map them to a section of the grid.
        //With this mapped grid value we can compare it to the mapped grid value of this gameObject
        //to see whether they are occupying the same grid space. If they are then we say that they
        //are colliding
        if(gameView.getMappedScreenX(enemyX) == gameView.getMappedScreenX(xPosition) &&
            gameView.getMappedScreenY(enemyY) == gameView.getMappedScreenY(yPosition) ){
            v.vibrate(2000);
            //Given that we deal different amounts of damaged based on what type of collision is
            //occuring, we need to examine the directions of the ships so that we can determine
            //exactly what type of collision has occured.

            //We first check whether the game object that has been passed in is a ship.
            //While this will always be true for this iteration of the product, it will allow
            //us to easily extend the collision logic to accomodate for cannonballs when they are
            //introduced in version 2
            if(testObject instanceof Ship){
                int enemyDirection = ((Ship) testObject).getDirection();
                int playerDirection = ((Ship) this).getDirection();

                //Case 1: Head on collision, detected by the fact that the two players are facing
                //complete opposite directions. In this case both players take the the opponents
                //health as damage. Meaning that whichever player has the highest health will survive
                //the collision
                if(Math.abs(enemyDirection - playerDirection) == 2){
                    this.decreaseHealth(testObject.getHealth());
                    testObject.decreaseHealth(this.getHealth());
                }

                //Case 2: Rear end, detected by the fact that the two players are facing the same
                //direction. In the case of rear-ending the player that got rear-ended is killed
                //while the player that did the rear-ending doesn't take any damage
                else if(enemyDirection == playerDirection){
                    switch(playerDirection){
                        //Because we need to figure out who did the rear-ending and who was rear-
                        //ended we need to examine the players directions so we can figure out how
                        //to determine this. For example, if both players are facing right, eg:
                        // Ship A -->     Ship B -->
                        //We can figure out which ship is ship A by comparing their x coordinates.
                        //The ship with the small x coordinate will be to the left of the other ship
                        //and thus will be the one rear-ending the other ship.

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
                //Case 3: One ship must be T-boning the other. Similar to how rear-ending was dealt
                //with, the player that is being rear-ended is killed and the player that did the
                //rear-ending takes no damage. The way that we figure out which player played
                //which roles is achieved in the exact same fashion to rear-ending except the
                //directions are different.
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

    public int getyPosition() {
        return yPosition;
    }

    public int getxPosition() {
        return xPosition;
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
