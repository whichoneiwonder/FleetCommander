package com.project.jaja.fleetcommander;

/**
 * Created by James on 19/10/2014.
 */
public interface Observable {
    public void notifyObservers();
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
}
