package com.project.jaja.fleetcommander;

/**
 * Created by James McLaren on 8/09/2014.
 */
public interface Firing {

    //Naturally we want all of our firing units to be able to shoot
    public void shoot();

    //We also need to be able to calculate whether or not a unit is in shooting range
    public boolean calculateShootingRange(GameObject target);

}
