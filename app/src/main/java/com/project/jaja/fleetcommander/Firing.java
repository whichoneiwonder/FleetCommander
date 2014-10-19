package com.project.jaja.fleetcommander;

import android.os.Vibrator;


/**
 * Created by James on 8/09/2014.
 */
public interface Firing {

    //Naturally we want all of our firing units to be able to shoot
    public void shoot(GameObject target, Vibrator v);

    //We also need to be able to calculate whether or not a unit is in shooting range
    public void calculateShootingRange(GameObject target, Vibrator v);


}
