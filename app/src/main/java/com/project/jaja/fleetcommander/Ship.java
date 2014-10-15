package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by avnishjain and jmcma on 19/09/14.
 */
public class Ship extends GameObject implements Movable, Firing {



    private ArrayList<Integer> xCoords;
    private ArrayList<Integer> yCoords;

    private Panel panel;

    //List of available speeds
    private int [] speeds = {-1,0,1};
    //Randomiser for the speed (will not be used later on)
    private Random random;

    //boolean whether or not the ship is selected
    private boolean shipSelect;

    //offset for border cases (not currently used)
    public final static int pixelOffset = 5;

    private Path path;

    private long time;

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

    /**ShipSprite Constructor for specifying all but speed
     *
     * @param gameView  - the view it will be attached to
     * @param map       - the Bitmap to that is the sprite image
     * @param xPosition - position in x
     * @param yPosition - position in y
     */
    public Ship(GameView gameView, Bitmap map, int xPosition, int yPosition, int health, Panel panel){
        this.gameView = gameView;
        this.map = map;
        path = new Path();
        this.panel = panel;

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

        this.direction = 0;
        this.shipSelect = false;

        this.xCoords = new ArrayList<Integer> ();
        this.yCoords = new ArrayList<Integer> ();

        this.health = health;

        time = System.currentTimeMillis();


    }
    //Default constructor
    public Ship(){

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
            return R.drawable.ship_right;

        }
        return R.drawable.select_ship_right;
    }

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
     * TODO - move this update method into the ship interface/classes
     */
    @Override
    public void update(){
        int centreX = xPosition + map.getWidth()/2;
        int centreY = yPosition + map.getHeight()/2;
        checkEdges();
        // move the ship by increments of its speed
        // amd update path to draw
        if(xCoords.size() > 0) {
            //create new path starting from current position
            path = new Path();
            path.moveTo(xPosition + (map.getWidth() / 2),
                    yPosition + (map.getHeight() / 2));
            //add a line to each waypoint
            for ( int i = 0; i< xCoords.size(); i++){
                path.lineTo(xCoords.get(i), yCoords.get(i));
            }

            calculateNextSpeed(xCoords.get(0), yCoords.get(0), centreX, centreY);


            //setXPosition(xCoords.get(0)- map.getWidth()/2);
            //setYPosition(yCoords.get(0)-map.getHeight()/2);

            xPosition += xSpeed;
            yPosition += ySpeed;


           if(Math.abs(centreX - xCoords.get(0)) <1 && Math.abs(centreY - yCoords.get(0)) <1){
               xCoords.remove(0);
               yCoords.remove(0);
           }

            direction = getDirection();
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

    /**OnDraw method called when the sprite is to be drawn to the canvas view
     *
     * @param canvas - the canvas to be drawn on
     */
    public void onDraw(Canvas canvas) {
        // paint to color ship's path with
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE );


        //draw the ship's path
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
        //restore the canvas to its original orientation
        canvas.restore();



    }

    public void clearAllButHead(){

        if(xCoords.size() <= 0){
            return;
        }
        int firstX = xCoords.get(0);
        int firstY = yCoords.get(0);
        xCoords.clear();
        yCoords.clear();
        xCoords.add(firstX);
        yCoords.add(firstY);
    }

    public void onMoveEvent(MotionEvent event){
        //ignore if it's not a move action


        if(event.getAction()!= MotionEvent.ACTION_MOVE){
            return;
        }
        int screenX = gameView.getMappedScreenX((int) event.getX());
        int screenY = gameView.getMappedScreenY((int) event.getY());
        //append the new coordinates to the path
        if(xCoords.isEmpty()){
            time = System.currentTimeMillis();
        }
        xCoords.add(screenX);
        yCoords.add(screenY);


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


    //ACCESSOR AND MUTATORs

    //Methods implemented from the Firing interface
    @Override
    public void shoot(){

    }

    @Override
    public void damage(){

    }

    @Override
    public boolean calculateShootingRange(GameObject target){

        //We first need to calculate the distance between the current ship and the target
        Location targetLoc = target.getLoc();
        Location userLoc = this.getLoc();

        double xDistance = targetLoc.getX() - userLoc.getX();
        double yDistance = targetLoc.getY() - userLoc.getY();

        //We also need to calculate whether or not the ship lies within the firing cones
        double angleBetween = Math.toDegrees(Math.atan2(yDistance, xDistance));

        if(angleBetween == 0 )
            if(true){
                return true;
            } else{
                return false;
            }

        return false;

    }

    //Methods implemented from the Movable interface
    @Override
    public void makeMove(){

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
