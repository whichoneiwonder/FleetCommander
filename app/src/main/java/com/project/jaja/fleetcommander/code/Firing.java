package com.project.jaja.fleetcommander.code;

/**
 * Created by James on 8/09/2014.
 */
public interface Firing {

    //Naturally we want all of our firing units to be able to shoot
    public void shoot();

    //This method allows us to calculate the damage that our unit has taken
    public void damage();

    //We also need to be able to calculate whether or not a unit is in shooting range
    public void calculateShootingRange();


}
