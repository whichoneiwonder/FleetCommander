package com.project.jaja.fleetcommander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Vibrator;
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

    //Integer value holding the number of ships created
    private int numShipsCreated;

    //An integer dictating how many ships we create
    private int numShipsInGame;

    //As the grid sizes depend on the size of the screen (to ensure consistency across multiple
    //devices, we need to keep track of the screen width and height
    public int screenheight;
    public int screenwidth;

    //The score panel at the top of the screen
    private Panel panel;

    //A reference to the Android vibration function allowing us to provide haptic feedback to the
    //player
    private Vibrator v;

    //As we render the player ships at the top of the screen and the enemy ships at the bottom
    //of the screen we need to keep reference to the x and y values of these points
    private int buttonLeftX;
    private int buttonTopY;
    private int buttonRightX;
    private int buttonBottomY;

    //Allowing us to keep track of the information pertaining to the grid superimposed on
    //the GameView
    private int gridLeft, gridRight, gridTop, gridBottom;
    public final static int numXGridPoints = 10;
    public final static int numYGridPoints = 15;

    //We also need to know which gridspaces we start rendering player and enemy ships in
    public final static int playerXStartingGrid = 1;
    public final static int playerYStartingGrid = 1;
    public final static int enemyXStartingGrid = 1;
    public final static int enemyYStartingGrid = 13;


    //a register
    private Ship shipReceivingInput = null;

    //Keeping track of the last player click
    private long lastClick;

    //Keeping track of the two players in the game
    private Player me;
    private Player enemy;

    //Keeping track of whether or not a click has occured
    private boolean noClick = false;

    /**
     * Constructor of the view
     * @param context -> Context of the game which comes from NewGameActivity
     * @param me Reference to the current user's player object
     * @param enemy Reference to the opponent's player object
     * @param numShipsInGame An integer determing how many ships should be rendered when the game
     *                       starts. This is on a per-player basis, so if numShipsInGame == 3 then
     *                       6 ships will be rendered. 3 for the Player and 3 for the Opponent
     */
    public GameView(Context context, Player me, Player enemy, int numShipsInGame){
        super(context);

        //Creates the thread
        thread = new GameLoopThread(this);

        //Players
        this.me = me;
        this.enemy = me;

        //Number of ships to create
        this.numShipsInGame = numShipsInGame;


        //Accessing the current Android window
        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //Accessing the phone's vibrate function
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        //Accessing the window's display and setting necessary properties
        Display display = window.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Getting information about the player's screen
        screenheight = size.y;
        screenwidth = size.x;

        //Instantiating the panel which sits at the top of the screen
        panel = new Panel(this);

        //Determing the grid spaces based on screen size
        gridTop = (int) (1.1*panel.getHeight());
        gridBottom = (int) (screenheight * 0.95);
        gridLeft = (int) (screenwidth * 0.05);
        gridRight =  (int) (screenwidth * 0.95);

        //This keeps track of how many ships have been created (different from numShipsInGame), as
        //we have not called populatShips() yet this should be 0
        numShipsCreated = 0;

        //Creates all the ships
        populateShips();

        //We need to access the surfaceHolder so that we can add a callback methods,
        // surfaceCreated(), surfaceChanged() and surfaceDestroyed()
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            /**
             *  When the surface is created when the game is about the begin
             * @param holder --> The holder that holds everything together
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
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
        ArrayList<Ship> blue_ships = new ArrayList<Ship>();
        ArrayList<Ship> red_ships = new ArrayList<Ship>();

        for(int i = 0; i < numShipsInGame; i++){
            blue_ships.add(newShip(R.drawable.ship_right, playerXStartingGrid));
            red_ships.add(newShip(R.drawable.enemy_ship_right, enemyXStartingGrid));
        }

        if(me.getShipColour().equals("blue")){
            me.setFleet(blue_ships);
            enemy.setFleet(red_ships);
        } else{
            me.setFleet(red_ships);
            enemy.setFleet(blue_ships);
        }
    }

    /**
     *  Creates a sprite of a ship
     *
     * @param drawable --> R.drawable.id that is generated by Android
     * @param startingX The starting x coordinate of the ship
     * @return newShip, the newly created ship
     */
    protected Ship newShip(int drawable, int startingX){
        //sets the image of the ship to the one specified
        map = BitmapFactory.decodeResource(getResources(), drawable);

        //Creates the new ship at the specified location
        Ship newShip = new Ship(this, map, 110 + numShipsCreated*10, 100, 100,panel);
        numShipsCreated++;
        return newShip;
    }

    /**
     * @param canvas The canvas on which the pause button should be drawn
     * A method to draw the pause button onto the passed in canvas
     */

    protected void renderPauseButton(Canvas canvas){
        Paint button = new Paint();
        button.setColor(Color.WHITE);
        button.setStrokeWidth(0);

        int pixel_offset = 15;

        Paint buttonText = new Paint();
        buttonText.setColor(Color.BLACK);
        buttonText.setStrokeWidth(10);
        buttonText.setTextSize((buttonBottomY - buttonTopY)/3 );

        buttonLeftX = pixel_offset;
        buttonTopY = pixel_offset;
        buttonRightX = panel.getWidth()/5;
        buttonBottomY = panel.getHeight() - pixel_offset;

        canvas.drawRect(buttonLeftX, buttonTopY, buttonRightX, buttonBottomY, button);

        //We change the text of the button based on the current state of the game
        if(panel.isPaused()) {
            canvas.drawText("Resume", buttonLeftX + 5, (buttonBottomY - buttonTopY)/2+pixel_offset, buttonText);
        }
        else{
            canvas.drawText("Pause", buttonLeftX + 5, (buttonBottomY - buttonTopY)/2+pixel_offset, buttonText);
        }
    }

    /**
     * A method to determine whether the pause button is being clicked
     * @param event The motion event which we must interpret
     * @return true if the button is being clicked
     */
    public boolean isClickingButton(MotionEvent event){
        if(event.getX() > buttonLeftX && event.getX() < buttonRightX && event.getY() > buttonTopY && event.getY() < buttonBottomY){
            return true;
        }
        return false;
    }


    /**
     * Handles all the rendering of the view
     *
     * @param canvas -> Canvas to render images on
     */
    protected void onDraw(Canvas canvas){
        //Sets the background to the RGB Value
         canvas.drawColor(Color.rgb(0,153,204));

        panel.onDraw(canvas);
        panel.onDraw(canvas);
        renderPauseButton(canvas);

        //Abstract to a function and potentially in the wrong place (should be in ShipSprite)
        //If the ship bounces of an edge it changes direction
        ArrayList<Ship> myShips = me.getFleet();
        for(int i = 0; i < myShips.size(); i++){

            Ship myShip = myShips.get(i);

            //gets the image needed to be displayed based on the direction
            int resourceID = myShip.getDirectionID(myShip.getDirection());
            //sets the image of the ship to the specified image
            myShip.setMap(BitmapFactory.decodeResource(getResources(), resourceID));

            //draws the ship onto the canvas

            //myShip.onDraw(canvas);
            //Log.d("SHIP DRAWING", "MY SHIP IS BEING DRAWN");

            //This will eventually be looping through all GameObjects, not just ships
            ArrayList<Ship> enemyShips = enemy.getFleet();
            for(int j = 0; j < enemyShips.size(); j++) {

                Ship enemyShip = enemyShips.get(j);
                //enemyShip.onDraw(canvas);
                //Log.d("SHIP DRAWING", "ENEMY SHIP IS BEING DRAWN");


                if(enemyShip.stillAlive()) {
                    myShip.detectCollision(enemyShip, v);
                } else{
//                    enemy.removeShipFromFleet(enemyShip);
                }
            }
        }
    }

    /**
     * As we have implemented a grid system, we need a method that can convert an x coordinate
     * into a grid number
     * @param screenX the player's x coordinate on the screen
     * @return the x value of the grid space that the player is occupying
     */
    public int getGridXFromScreenX(int screenX){
        if(screenX >= gridRight){
            return numXGridPoints -1;
        }
        if (screenX <= gridLeft){
            return 0;
        }

        double spaceBetweenGridPoints = (gridRight - gridLeft)/((double) numXGridPoints);
        return (int)(0.5 + (screenX - gridLeft)/spaceBetweenGridPoints);

    }

    /**
     * This method is the same as getGridXFromScreenX but it operates in the Y axis
     * @param screenY the player's y coordinate on the screen
     * @return the y value of the grid space that the player is occupying
     */
    public int getGridYFromScreenY(int screenY){
        if(screenY >= gridBottom){
            return numYGridPoints -1;
        }
        if (screenY <= gridTop){
            return 0;
        }

        double spaceBetweenGridPoints = (gridBottom - gridTop)/((double) numYGridPoints);
        return (int)(0.5 + (screenY - gridTop)/spaceBetweenGridPoints);

    }

    /**
     * Likewise, we need to be able to convert a grid x value to a screen x value
     * @param gridX the x value of the grid space that the player is occupying
     * @return the x value on the screen
     */
    public int getScreenXFromGridX(int gridX){
        double spaceBetweenGridPoints = (gridRight - gridLeft)/((double) numXGridPoints);
        return gridLeft + (int) (spaceBetweenGridPoints * gridX);
    }

    /**
     * Same as getScreenXFromGridX but operates in the Y axis
     * @param gridY the y value of the grid space the player is occupying
     * @return the y value of where the player is on the screen
     */
    public int getScreenYFromGridY(int gridY){
        double spaceBetweenGridPoints = (gridBottom - gridTop)/((double) numYGridPoints);
        return gridTop + (int) (spaceBetweenGridPoints * gridY);
    }

    /**
     * Converts an integer value into the screen x value the player is occupying
     * @param eventX the event x coordinate
     * @return the Screen X value
     */
    public int getMappedScreenX(int eventX){
        int gridX = getGridXFromScreenX(eventX);
        return getScreenXFromGridX(gridX);
    }

    /**
     * Converts an integer value into the screen y value the player is occupying
     * @param eventY the event y coordinate
     * @return the Screen Y value
     */
    public int getMappedScreenY(int eventY){
        int gridY = getGridYFromScreenY(eventY);
        return getScreenYFromGridY(gridY);
    }

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
        String test = null;
        if (noClick) {
            test = "true";
        } else {
            test = "false";
        }
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis();
            synchronized (getHolder()) {
                if (isClickingButton(event) && !noClick) {
                    panel.clickPause();
                }
            }

        }

        if (!panel.isPaused()) {
            //if this is the first part of a touch event
            if (event.getAction() == event.ACTION_DOWN) {

                //the distance between this event and the ships
                int minDistance = Integer.MAX_VALUE;//initialised to max, so we can find min
                int distance;

                //find the closest ship to where you've clicked
                for (Ship ship : me.getFleet()) {
                    distance = Math.abs(ship.getXPosition() + ship.getMap().getWidth() / 2
                            - (int) event.getX()) +
                            Math.abs(ship.getYPosition() + ship.getMap().getHeight() / 2
                                    - (int) event.getY());
                    if (distance < minDistance) {
                        minDistance = distance;
                        shipReceivingInput = ship;

                    }
                }
                //select a ship only if you've clicked pretty close to it
                if (minDistance > 80) {
                    shipReceivingInput = null;
                }
                //reset the path
                if (shipReceivingInput != null) {

                    shipReceivingInput.clearAllButHead();
                    //indicate selection
                    shipReceivingInput.setShipSelect(true);
                }


                return true;
            }

            //if you're dragging your finger
            if (event.getAction() == MotionEvent.ACTION_MOVE && shipReceivingInput != null) {

                shipReceivingInput.onMoveEvent(event);

                return true;
            }

            //when you release your finger reset which ship you're interacting with
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //indicate disselection
                if (shipReceivingInput != null) {
                    shipReceivingInput.setShipSelect(false);
                }

                shipReceivingInput = null;
                return true;
            }

        }
        return true;
    }

    /**
     * Returns this panel
     * This assists us in checking if the game is paused or playing in the NewGameActivity
     * @return panel this Game panel
     */
    public Panel getPanel() {
        return panel;
    }

    /**
     * Sets the value of noClick
     * @param noClick the value we are setting noClick to
     */
    public void setNoClick(boolean noClick) {
        this.noClick = noClick;
    }


}