package com.project.jaja.fleetcommander;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 */
public class Ship extends GameObject implements Firing, Movable {

    private int health;
    private double shootableRange;
    private ArrayList<Item> items;

    public Ship(int health, double shootableRange){
        this.health = health;
        this.shootableRange = shootableRange;
    }

    //Default constructor
    public Ship(){

    }

    //Methods implemented from the Firing interface
    @Override
    public void shoot(){

    }

    @Override
    public void damage(){

    }

    @Override
    public boolean calculateShootingRange(GameObject target){

        //We first need to calculate the distance between the current ship and the target
        Location targetLoc = target.getLoc();
        Location userLoc = this.getLoc();

        double xDistance = targetLoc.getX() - userLoc.getX();
        double yDistance = targetLoc.getY() - userLoc.getY();

        //We also need to calculate whether or not the ship lies within the firing cones
        double angleBetween = Math.toDegrees(Math.atan2(yDistance, xDistance));

        if(angleBetween == 0 )
        if(true){
            return true;
        } else{
            return false;
        }

        return false;

    }

    //Methods implemented from the Movable interface
    @Override
    public void makeMove(){

    }

    @Override
    public void update(){

    }
}
