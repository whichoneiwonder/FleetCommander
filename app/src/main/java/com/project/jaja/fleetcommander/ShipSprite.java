package com.project.jaja.fleetcommander;

//Image type
import android.graphics.Bitmap;

//Android Canvas functionality
import android.graphics.Canvas;

/** ShipSprite Class embodies the actual image of a ship
 * that will be rendered to the canvas, as well as its position
 *
 *
 * Created by avnishjain and jmcma on 19/09/14.
 *
 * As of 25/09/14 the speed parameters and the update logic should be
 * Moved to into ship and the classes that implement it.
 * Currently it is here for testing only
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


    }

    /**Update method called each frame
     * TODO - move this update method into the ship interface/classes
     */
    private void update(){
        //Log.i("Debugging", "Ship Position : (" + xPosition + ", " + yPosition + ")\n" );
        //Log.i("Debugging", "Edge of Screen : " + (gameView.getWidth() - map.getWidth()) + "\n\n");


        // check if the ship has reached the edge of the screen
        if (xPosition > gameView.getWidth() - map.getWidth() - xSpeed ||
                xPosition + xSpeed < 0) {
            xSpeed = -xSpeed ;
        }
        if (yPosition > gameView.getHeight() - map.getHeight() - ySpeed ||
                yPosition + ySpeed < 0) {
            ySpeed = -ySpeed ;
        }
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

    public boolean isColliding (float x, float y) {
        return x > xPosition &&
                x < xPosition + map.getWidth() &&
                y > yPosition &&
                y < yPosition + map.getHeight();
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
}
