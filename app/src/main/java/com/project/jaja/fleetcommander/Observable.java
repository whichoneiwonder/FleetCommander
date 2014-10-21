package com.project.jaja.fleetcommander;

/**
 * Created by James on 19/10/2014.
 */
public interface Observable {
    /**
     * Notify all observers subscribed to this object
     */
    public void notifyObservers();

    /**
     * Subscribe and observer to this object
     * @param o the observer to add
     */
    public void addObserver(Observer o);

    /**
     * Remove and observer from this object
     * @param o the observer to remove
     */
    public void removeObserver(Observer o);
}
