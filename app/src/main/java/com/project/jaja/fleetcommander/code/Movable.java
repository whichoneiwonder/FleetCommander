package com.project.jaja.fleetcommander.code;

/**
 * Created by James on 8/09/2014.
 */
public interface Movable {

    //Allowing the object to move, will probably need to be adjusted to take in an endpoint/speed
    public void makeMove();

    //We need to update the units after movement
    public void update();
}
