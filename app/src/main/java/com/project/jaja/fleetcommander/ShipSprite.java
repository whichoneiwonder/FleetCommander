package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by avnishjain and jmcma on 19/09/14.
 */
public class ShipSprite {

    //the View the sprite will be rendered into
    private GameView gameView;

    //the bitmap image of the sprite itself:
    private Bitmap map;

    // Speed X and Y values
    private int xSpeed;
    private int ySpeed;

    //position X and Y Values
    private int xPosition;
    private int yPosition;

    //offset for border cases (not currently used)
    public final static int pixelOffset = 5;

    //Integer direction values and costants
    private int direction;
    public final static int UP = 0;
    public final static int DOWN = 2;
    public final static int LEFT = 3;
    public final static int RIGHT = 1;

    /**ShipSprite Constructor for specifying all but speed
     *
     * @param gameView  - the view it will be attached to
     * @param map       - the Bitmap to that is the sprite image
     * @param xPosition - position in x
     * @param yPosition - position in y
     */
    public ShipSprite(GameView gameView, Bitmap map, int xPosition, int yPosition){
        this.gameView = gameView;
        this.map = map;

        // set position randomly (not currently used)
        //this.xPosition = (int)(Math.random() * (gameView.getWidth() - map.getWidth())) + 1;
        //this.xPosition = (int)(Math.random() * (gameView.getHeight() - map.getHeight())) + 1;

        // set position by input
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        // set speed randomly (not currently used)
        this.xSpeed = (int) (Math.random()*10)+1;
        this.ySpeed = (int) (Math.random()*10)+1;

        // set speed (not currently used)
        //this.xSpeed = 1;
        //this.ySpeed = 1;

        this.direction = 2;


    }

    public int getDirectionID(int direction){
        if(direction == UP){
            return R.drawable.player_up;
        }
        if(direction == RIGHT){
            return R.drawable.player_right;
        }
        if(direction == DOWN){
            return R.drawable.player_down;
        }

        return R.drawable.player_left;

    }

    public int getDirection(){
        if(xSpeed > 0 && ySpeed > 0 || xSpeed > 0 && ySpeed < 0){
            return RIGHT;
        }
        if(xSpeed < 0 && ySpeed > 0 || xSpeed < 0 && ySpeed < 0){
            return LEFT;
        }
        if(xSpeed == 0 && ySpeed > 0)
            return DOWN;

        if(xSpeed == 0 && ySpeed < 0)
            return UP;

        return direction;
    }

    /**Update method called each frame
     * TODO - move this update method into the ship interface/classes
     */
    private void update(){
        Log.i("Debugging", "Ship Position : (" + xPosition + ", " + yPosition + ")\n");
        Log.i("Debugging", "Edge of Screen : " + (gameView.getWidth() - map.getWidth()) + "\n\n");

       /* if (xPosition > gameView.getWidth() - map.getWidth() - xSpeed || xPosition + xSpeed < 0) {
            xSpeed = -xSpeed ;
        }

        if (yPosition > gameView.getHeight() - map.getHeight() - ySpeed || yPosition + ySpeed < 0) {
            ySpeed = -ySpeed ;
        }*/

        // move the ship by increments of its speed
        xPosition = xPosition + xSpeed;
        yPosition = yPosition + ySpeed;

    }

    /**OnDraw method called when the sprite is to be drawn to the canvas view
     *
     * @param canvas - the canvas to be drawn on
     */
    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(map, xPosition , yPosition, null);
    }

    /**
     *
     * Checks to see if any given (x,y) position
     * is within the bitmap region
     *
     * @param x -- Other objects x position
     * @param y -- Other objects y position
     * @return true
     */
    public boolean isColliding (float x, float y) {
        return x > xPosition && x < xPosition + map.getWidth() && y > yPosition && y < yPosition + map.getHeight();
    }

    //ACCESSORS AND MUTATORS

    public int getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public Bitmap getMap() {
        return map;
    }

    public void setMap(Bitmap map) {
        this.map = map;
    }

}
