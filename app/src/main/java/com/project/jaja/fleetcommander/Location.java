package com.project.jaja.fleetcommander;

/**
 * Created by anty on 5/09/14.
 */
public class Location {

    //The x and y values of the location
    private int x;
    private int y;

    /**
     * Standard constuctor for a location
     * @param x the x value of the location
     * @param y the y value of the location
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //=============================================================================================
    //                          ACCESSOR AND MUTATOR METHODS
    //=============================================================================================

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveX(int amount) {
        this.x += amount;
    }

    public void moveY(int amount) {
        this.y += amount;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
