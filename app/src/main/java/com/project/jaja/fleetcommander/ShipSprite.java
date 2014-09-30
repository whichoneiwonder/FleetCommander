package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.Random;

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

    private ArrayList<Integer> xCoords;
    private ArrayList<Integer> yCoords;

    //List of available speeds
    private int [] speeds = {-1,0,1};
    //Randomiser for the speed (will not be used later on)
    private Random random;

    //boolean whether or not the ship is selected
    private boolean shipSelect;

    //offset for border cases (not currently used)
    public final static int pixelOffset = 5;

    private Path path;

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
        path = new Path();

        // set position randomly (not currently used)
        //this.xPosition = (int)(Math.random() * (gameView.getWidth() - map.getWidth())) + 1;
        //this.xPosition = (int)(Math.random() * (gameView.getHeight() - map.getHeight())) + 1;

        // set position by input
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        // set speed randomly (not currently used)
        random = new Random();
        //setRandomSpeed();
        this.xSpeed = 0;
        this.ySpeed = 0;

        this.direction = 2;
        this.shipSelect = false;

        this.xCoords = new ArrayList<Integer> ();
        this.yCoords = new ArrayList<Integer> ();


    }

    public int getDirectionID(int direction){

        //Animations bugging out when path is involved

        /*if(shipSelect == false){
            if(direction == UP){
                return R.drawable.ship_up;
            }
            if(direction == RIGHT){
                return R.drawable.ship_right;
            }
            if(direction == DOWN){
                return R.drawable.ship_down;
            }

            return R.drawable.ship_left;
        }//end outer if
        else{
            if(direction == UP){
                return R.drawable.select_ship_up;
            }
            if(direction == RIGHT){
                return R.drawable.select_ship_right;
            }
            if(direction == DOWN){
                return R.drawable.select_ship_down;
            }

            return R.drawable.select_ship_left;

        }*/
        if(shipSelect == false){
            return R.drawable.player_left;
        }
        return R.drawable.select_ship_left;
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
        int centreX = xPosition + map.getWidth()/2;
        int centreY = yPosition + map.getHeight()/2;
        checkEdges();
        // move the ship by increments of its speed
        if(xCoords.size() > 0) {
            calculateNextSpeed(xCoords.get(0), yCoords.get(0), centreX, centreY);
            xPosition = xPosition + xSpeed;
            yPosition = yPosition + ySpeed;

            if(centreX == xCoords.get(0) && centreY == yCoords.get(0)){
               xCoords.remove(0);
               yCoords.remove(0);
            }
        }

    }

    public void calculateNextSpeed(int nextX, int nextY, int centreX, int centreY){
       xSpeed = nextX - centreX;
       ySpeed = nextY - centreY;

       if(xSpeed < 0){
           xSpeed = -1;
       }

       if(ySpeed < 0){
           ySpeed = -1;
       }

        if(xSpeed > 0){
            xSpeed = 1;
        }

        if(ySpeed > 0){
            ySpeed = 1;
        }
    }

    /**
     *  If the ship is on the edge of the view, then the speeds are reversed
     *  If not, then the ship maintains its speed and therefore direction.
     *
     */
    public void checkEdges(){
        if (getXPosition() > gameView.getWidth() - map.getWidth() - getXSpeed()) {
            setXPosition(gameView.getWidth() - map.getWidth() - getXSpeed());
            return;
        }
        if(getXPosition() + getXSpeed() < 0){
            setXPosition(getXSpeed());
            return;
        }

        if (getYPosition() > gameView.getHeight() - map.getHeight() - getYSpeed()) {
            setYPosition(gameView.getHeight() - map.getHeight() - getYSpeed());
            return;
        }

        if(getYPosition() + getYSpeed() < 0){
            setYPosition(getYSpeed());
        }

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

    public void setRandomSpeed() {
        this.xSpeed = speeds[random.nextInt(speeds.length)];
        this.ySpeed = speeds[random.nextInt(speeds.length)];

        if (xSpeed == 0 && ySpeed == 0){
            setRandomSpeed();
            return;
        }
    }

    public void setMap(Bitmap map) {
        this.map = map;
    }

    public boolean isShipSelect() {
        return shipSelect;
    }

    public void setShipSelect(boolean shipSelect) {
        this.shipSelect = shipSelect;
    }

    public void changeShipSelect(boolean shipSelect) { setShipSelect(!shipSelect);}

    public ArrayList<Integer> getxCoords() {
        return xCoords;
    }

    public ArrayList<Integer> getyCoords() {
        return yCoords;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
