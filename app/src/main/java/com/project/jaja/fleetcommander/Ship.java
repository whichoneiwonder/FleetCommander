package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;
import android.os.Vibrator;
import android.util.Log;

/**
 * A class representing the ships that are actually drawn in game
 * Created by avnishjain and jmcma on 19/09/14.
 */
public class Ship extends GameObject implements Movable, Firing {


    //These two arraylists keep track of the x and y coordinates of the player drawn
    //path for the ship
    private ArrayList<Integer> xCoords;
    private ArrayList<Integer> yCoords;

    //List of available speeds
    private int [] speeds = {-1,0,1};

    //boolean whether or not the ship is selected
    private boolean shipSelect;

    //offset for border cases (not currently used)
    public final static int pixelOffset = 5;

    //The path that the ship follows
    private Path path;

    //The panel in the Gameview, this is used a reference point for ship creation
    private Panel panel;

    //Integer direction values and constants
    private int direction;
    public final static int RIGHT = 0;
    public final static int DOWNRIGHT = 1;
    public final static int DOWN = 2;
    public final static int DOWNLEFT = 3;
    public final static int LEFT = 4;
    public final static int UPLEFT = 5;
    public final static int UP = 6;
    public final static int UPRIGHT = 7;

    public final static int SHOOTING_DAMAGE = 25;

    /**ShipSprite Constructor for specifying all but speed
     *
     * @param gameView  - the view it will be attached to
     * @param map       - the Bitmap to that is the sprite image
     * @param xPosition - position in x
     * @param yPosition - position in y
     */
    public Ship(GameView gameView, Bitmap map, int xPosition, int yPosition, int health, String color, Panel panel){
        this.gameView = gameView;
        this.map = map;
        path = new Path();
        this.panel = panel;


        // set position by input
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        //The ship starts with 0 speed in either direction
        this.xSpeed = 0;
        this.ySpeed = 0;

        //All ships are facing the same direction and no ship is select
        this.direction = 0;
        this.shipSelect = false;

        //Allows us to keep track of the path that the ship has taken
        this.xCoords = new ArrayList<Integer> ();
        this.yCoords = new ArrayList<Integer> ();

        //Setting the ships starting health
        this.health = health;
        this.color = color;


    }
    //Default constructor
    public Ship(){

    }

    /**
     * Returns the sprite relevant for the ships current condition. If the ship is not active
     * it will just be ship_right but if it is being selected then it will be select_ship_right
     * @return the relevant sprite
     */
    public int getDirectionID(){
        if(color.equals("blue")) {
            if (shipSelect == false) {
                return R.drawable.ship_right;
            }
            return R.drawable.select_ship_right;
        }
        else{
            if (shipSelect == false) {
                return R.drawable.enemy_ship_right;
            }
            return R.drawable.select_ship_right;
        }
    }

    /**
     * Gets the integer associated with the ships current direction based on it's x and y speeds
     * @return the ships direction
     */
    public int getDirection(){
        if(xSpeed > 0 && ySpeed == 0){
            return RIGHT;
        }
        if(xSpeed < 0 && ySpeed == 0 ){
            return LEFT;
        }
        if(xSpeed == 0 && ySpeed > 0)
            return UP;

        if(xSpeed == 0 && ySpeed < 0)
            return DOWN;

        if( xSpeed < 0 && ySpeed < 0)
            return DOWNLEFT;

        if(xSpeed > 0 && ySpeed < 0){
            return DOWNRIGHT;
        }
        if(xSpeed > 0 && ySpeed > 0){
            return UPRIGHT;
        }
        if(xSpeed < 0 && ySpeed > 0){
            return UPLEFT;
        }

        return direction;
    }

    /**Update method called each frame
     */
    @Override
    public void update(){
        //Getting the x and y coordinates of the center point of the ship
        int centreX = xPosition + map.getWidth()/2;
        int centreY = yPosition + map.getHeight()/2;

        // move the ship by increments of its speed
        // amd update path to draw
        if(xCoords.size() > 0) {
            //create new path starting from current position
            path = new Path();
            path.moveTo(xPosition + (map.getWidth() / 2),
                    yPosition + (map.getHeight() / 2));

            //add a line to each waypoint
            for ( int i = 0; i< xCoords.size(); i++){
                path.lineTo(gameView.getScreenXFromGridX(xCoords.get(i)), gameView.getScreenYFromGridY(yCoords.get(i)));
            }
            int firstScreenX = gameView.getScreenXFromGridX((xCoords.get(0)));
            int firstScreenY = gameView.getScreenYFromGridY((yCoords.get(0)));

            calculateNextSpeed(firstScreenX, firstScreenY, centreX, centreY);
            xPosition = xPosition + xSpeed;
            yPosition = yPosition + ySpeed;

            //If the ship is at the next point referenced in the xCoords and yCoords arraylist
            //then we no longer need to keep track of that coordinate
            if(Math.abs(centreX - firstScreenX) <1 && Math.abs(centreY - firstScreenY) <1){

               xCoords.remove(0);
               yCoords.remove(0);
            }

            //We also need to calculate which direction the ship is facing after moving
            direction = getDirection();
        }



    }

    /**
     * We also need to calculate the speed at which the ship will move based on where it is
     * now and where it will be next turn
     * @param nextX the x coordinate it will occupy after moving
     * @param nextY the y coordinate it will occupy after moving
     * @param centreX the x coordinate it is currently occupying
     * @param centreY the y coordinate it is currently occupying
     */
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
     * Checing whether or not the ship is colliding with the edge of the screen.
     * If it is then we redirect the ship
     */
    public void checkEdges(){

        int pixel_offset = 5;

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

        if(getYPosition() + getYSpeed() <= panel.getHeight()+pixel_offset){
            setYPosition(panel.getHeight()+pixel_offset);
        }

    }

    /**
     * If we want to reset the player's path, in the case that they want to draw another path,
     * we simply clear the arraylist
     */
    public void clearPath(){

        if(xCoords.size() <= 0){
            return;
        }
        xCoords.clear();
        yCoords.clear();
    }

//On Move Event Body

    public void onMoveEvent(MotionEvent event){
        //ignore if it's not a move action


        if(xCoords.size()>=10 ||event.getAction()!= MotionEvent.ACTION_MOVE){
            return;
        }

        //append the new coordinates to the path

        int gridX = gameView.getGridXFromScreenX((int) event.getX());
        int gridY = gameView.getGridYFromScreenY((int) event.getY());

        xCoords.add(gridX);
        yCoords.add(gridY);


    }



    /**OnDraw method called when the sprite is to be drawn to the canvas view
     *
     * @param canvas - the canvas to be drawn on
     */
    public void onDraw(Canvas canvas) {
        // paint to color ship's path with
        Paint paint = new Paint();
        Paint healthBar = new Paint();
        healthBar.setColor(Color.rgb(129,240, 135));
        if(color.equals("blue")) {
            paint.setColor(Color.BLUE);
        }
        else{
            paint.setColor(Color.RED);
        }
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE );

        //draw the ship's path

        path = new Path();
        path.moveTo(xPosition + (map.getWidth() / 2),
                yPosition + (map.getHeight() / 2));

        //add a line to each waypoint
        for ( int i = 0; i< xCoords.size(); i++){
            path.lineTo(gameView.getScreenXFromGridX(xCoords.get(i)), gameView.getScreenYFromGridY(yCoords.get(i)));
        }

        canvas.drawPath(path,paint);


        //calculate what rotation the ship is from due right
        float rotationDegrees = (direction*  -45);
        //save the orientation of the canvas
        canvas.save();
        //rotate canvas around the centre of the ship
        canvas.rotate( rotationDegrees, xPosition + (map.getWidth() / 2),
                yPosition + (map.getHeight() / 2));
        //draw the ship in rotated frame at its position
        canvas.drawBitmap(map, xPosition , yPosition, null);
        canvas.drawRect((float)xPosition+7,
                (float)yPosition+map.getHeight()/2 -1 ,
                (float) xPosition + map.getWidth()*health/100 - 10,
                (float)yPosition+map.getHeight()/2 +1,
                healthBar);
        //restore the canvas to its original orientation
        canvas.restore();



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

    //Methods implemented from the Firing interface
    @Override
    public void shoot(GameObject target, Vibrator v){


        target.decreaseHealth(SHOOTING_DAMAGE);


        v.vibrate(100);
    }

    /**
     * Calculates whether or not another GameObject is in shooting range of this ship
     * @param target the object we are testing
     * @return true if the object is in shooting range
     */
    @Override
    public void calculateShootingRange(GameObject target, Vibrator v){

        //We first figure out which gridspaces the player and enemy ships occupy
        int playerX = gameView.getGridXFromScreenX(xPosition);
        int playerY = gameView.getGridYFromScreenY(yPosition);

        int enemyX = gameView.getGridXFromScreenX(target.getxPosition());
        int enemyY  = gameView.getGridYFromScreenY(target.getyPosition());

        //Then we calculate how many grid spaces they are apart
        double xDistance = Math.abs(playerX - enemyX);
        double yDistance = Math.abs(playerY - enemyY);

        //Now we look at the direction of the ship to calculate what the distance must
        //be for the enemy ship to be in range
        if(this.direction == 0 || this.direction == 4){
            //Ship is facing left or right, thus we want large y small x
            if(yDistance <= 2 &&yDistance >0 && xDistance <= 1){
                Log.d("Shooting", "Something is being shot");
                shoot(target, v);
            }
        } else if(this.direction == 2 || this.direction == 6){
            //Ship is facing up or down, thus we want large x and small y
            if(xDistance <= 2 && xDistance >0&& yDistance <= 1){
                Log.d("Shooting", "Something is being shot");
                shoot(target, v);
            }

        }

    }

    //=============================================================================================
    //                          ACCESSOR AND MUTATOR METHODS
    //=============================================================================================

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

    public void setxCoords(ArrayList<Integer> xCoords){ this.xCoords = xCoords; }

    public void addxCoord(int xCoord){ this.xCoords.add(xCoord);}

    public ArrayList<Integer> getyCoords() {
        return yCoords;
    }

    public void setyCoords(ArrayList<Integer> yCoords){ this.yCoords = yCoords;}

    public void addyCoord(int yCoord){ this.yCoords.add(yCoord);}

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
