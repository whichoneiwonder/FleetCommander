package com.project.jaja.fleetcommander;

/**
 * Created by anty on 5/09/14.
 */
public class Location {
    private int x;
    private int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

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
