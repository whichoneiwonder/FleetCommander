package com.project.jaja.fleetcommander.code;

import java.util.ArrayList;

/**
 * Created by anty on 5/09/14.
 */
public class Ship extends GameObject implements Firing, Movable {

    private int health;
    private ArrayList<Item> items;

    public Ship(int health){
        this.health = health;
    }

    //Methods implemented from the Firing interface
    @Override
    public void shoot(){

    }

    @Override
    public void damage(){

    }

    @Override
    public void calculateShootingRange(){

    }

    //Methods implemented from the Movable interface
    @Override
    public void makeMove(){

    }

    @Override
    public void update(){

    }
}
