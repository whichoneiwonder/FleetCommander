package com.project.jaja.fleetcommander.code;

/**
 * Created by James on 8/09/2014.
 */
public class Item extends GameObject {

    public Item(){

    }

    //A method that allows the item to be picked up, will most likely need to be changed
    //so that it can take in a position of a ship or even a ship itself
    public boolean pickUp(){
        return true;
    }
}
