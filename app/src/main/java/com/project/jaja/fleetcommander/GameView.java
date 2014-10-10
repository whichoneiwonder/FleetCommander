package com.project.jaja.fleetcommander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;


/**
 * Written by avnishjain
 *
 * This class incorporates the View, and therefore
 * the rendering and UI aspects of the game.
 */
public class GameView extends SurfaceView {

    //Bitmap that captures the image of the ship
    private Bitmap map;

    //Surface holder that holds everything together
    private SurfaceHolder surfaceHolder;

    //Thread that holds the game loop logic
    private GameLoopThread thread;

    //List of all the instantiated ships
    private ArrayList<Ship> ships;

    //Integer value holding the number of ships created
    private int numShips;

    public int screenheight;
    public int screenwidth;

    private long lastClick;


    /**
     * Constructor of the view
     * @param context -> Context of the game which comes from NewGameActivity
     */
    public GameView(Context context){
        super(context);

        //Creates the tread
        thread = new GameLoopThread(this);

        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenheight = size.y;
        screenwidth = size.x;
        Log.i("Dimensions", "Screenheight: " + screenheight + "\nScreen Width: " + screenwidth);
        ships = new ArrayList<Ship>();
        numShips = 0;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            /**
             *  When the surface is created when the game is about the begin
             * @param holder --> The holder that holds everything together
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                //Creates all the ships
                populateShips();

                //Starts the thread and game loop
                thread.setGameState(true);
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                /*Does nothing yet*/
            }

            /**
             *  When the game is exited, this method is called
             * @param holder  --> The holder that holds everything together
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                //when the surface is destroyed, try restarting
                //thread if in case the orientation of the screen has
                //changed
                boolean test = true;
                thread.setGameState(false);
                while (test) {
                    try {
                        //closes the last active thread and starts a new thread if possible
                        thread.join();
                        test = false;
                    } catch (InterruptedException e) {
                        /* Do nothing */
                    }
                }


            }
        });


    }

    /**
     *  Method call that populates the ArrayList.
     *  Method allows for the multiple ships to be created in
     *  one place
     */
    protected void populateShips(){
        ships.add(newShip(R.drawable.ship_right));
        ships.add(newShip(R.drawable.ship_right));
        //ships.add(newShip(R.drawable.player_ship));
    }

    /**
     *  Creates a sprite of a ship
     *
     * @param drawable --> R.drawable.id that is generated by Android
     * @return the Sprite of the ship
     */
    protected Ship newShip(int drawable){
        //sets the image of the ship to the one specified
        map = BitmapFactory.decodeResource(getResources(), drawable);

        //Creates the new ship at the specified location

        Ship newShip = new Ship(this, map, (numShips * 110)+30, 10);
        numShips++;
        return newShip;
    }

    /**
     * Handles all the rendering of the view
     *
     * @param canvas -> Canvas to render images on
     */
    protected void onDraw(Canvas canvas){
        //Sets the background to the RGB Value
         canvas.drawColor(Color.rgb(0,153,204));

        //Abstract to a function and potentially in the wrong place (should be in ShipSprite)
        //If the ship bounces of an edge it changes direction
        for(Ship ship : ships){
            //gets the image needed to be displayed based on the direction
            int resourceID = ship.getDirectionID(ship.getDirection());
            //sets the image of the ship to the specified image
            ship.setMap(BitmapFactory.decodeResource(getResources(), resourceID));

            //draws the ship onto the canvas
            ship.onDraw(canvas);

            //This will eventually be looping through all GameObjects, not just ships
            for(GameObject secondShip: ships){
                //We don't want to check if a ship is colliding with itself
                if(secondShip == ship){
                    continue;
                }

                if(secondShip.stillAlive()) {
                    ship.detectCollision(secondShip);
                }
                /*else{
                    ships.remove(secondShip);
                }*/
            }
        }






    }

    //a register
    Ship shipReceivingInput = null;
    /**
     * Method used when a ship is tapped on
     * Currently it is just removed from rendering, but this one was for
     * debugging principles. It is not the main function of the game
     *
     * @param event --> Touch event
     * @return --> Boolean if there has been a touch or not
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (System.currentTimeMillis() - lastClick > 300) {
        lastClick = System.currentTimeMillis();
            synchronized (getHolder()) {

            }

        }
        //if this is the first part of a touch event
        if(event.getAction() == event.ACTION_DOWN) {

            //the distance between this event and the ships
            int minDistance = Integer.MAX_VALUE;//initialised to max, so we can find min
            int distance;

            //find the closest ship to where you've clicked
            for(Ship ship:ships) {
                distance = Math.abs(ship.getXPosition()+ship.getMap().getWidth()/2
                            - (int) event.getX()) +
                        Math.abs(ship.getYPosition() + ship.getMap().getHeight()/2
                                - (int) event.getY());
                if( distance < minDistance){
                    minDistance = distance;
                    shipReceivingInput = ship;

                }
            }
            //select a ship only if you've clicked pretty close to it
            if(minDistance >80 ){
                shipReceivingInput = null;
            }
            //reset the path
            if(shipReceivingInput!= null){
                shipReceivingInput.getxCoords().clear();
                shipReceivingInput.getyCoords().clear();
                //indicate selection
                shipReceivingInput.setShipSelect(true);
            }



            return true;
        }

        //if you're dragging your finger
        if(event.getAction() == MotionEvent.ACTION_MOVE && shipReceivingInput!= null){

            shipReceivingInput.onMoveEvent(event);

            return true;
        }

        //when you release your finger reset which ship you're interacting with
        if(event.getAction() == MotionEvent.ACTION_UP){
            //indicate disselection
            if(shipReceivingInput!= null){
                shipReceivingInput.setShipSelect(false);
            }

            shipReceivingInput = null;
            return true;
        }
        return true;
    }



}