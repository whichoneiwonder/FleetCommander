package com.project.jaja.fleetcommander;

/**
 * Created by James on 19/10/2014.
 */
public interface Observer {

    /**
     * A method to update the object being observed
     * @param o the object to update
     */
    public void update(Object o);
}
