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
    public int screenheight;
    public int screenwidth;

    private Panel panel;

    private Vibrator v;

    private int buttonLeftX;
    private int buttonTopY;
    private int buttonRightX;
    private int buttonBottomY;

    private int gridLeft, gridRight, gridTop, gridBottom;
    public final static int numXGridPoints = 10;
    public final static int numYGridPoints = 15;

    public final static int playerXStartingGrid = 1;
    public final static int playerYStartingGrid = 1;
    public final static int enemyXStartingGrid = 1;
    public final static int enemyYStartingGrid = 13;


    //a register
    private Ship shipReceivingInput = null;

    private long lastClick;

    //Keeping track of the two players in the game
    private Player me;
    private Player enemy;

    private boolean noClick = false;

    /**
     * Constructor of the view
     * @param context -> Context of the game which comes from NewGameActivity
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



        WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Display display = window.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenheight = size.y;
        screenwidth = size.x;


        panel = new Panel(this);



        gridTop = (int) (1.1*panel.getHeight());
        gridBottom = (int) (screenheight * 0.95);
        gridLeft = (int) (screenwidth * 0.05);
        gridRight =  (int) (screenwidth * 0.95);

        Log.i("Dimensions", "Screenheight: " + screenheight + "\nScreen Width: " + screenwidth);
        numShipsCreated = 0;

        //Creates all the ships
        populateShips();

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
     * Returns this panel
     * This assists us in checking if the game is paused or playing in the NewGameActivity
     * @return panel this Game panel
     */
    public Panel getPanel() {
        return panel;
    }

    public void setNoClick(boolean noClick) {
        this.noClick = noClick;
    }

    /**
     *  Method call that populates the ArrayList.
     *  Method allows for the multiple ships to be created in
     *  one place
     */
    protected void populateShips(){
        ArrayList<Ship> blue_ships = new ArrayList<Ship>();
        ArrayList<Ship> red_ships = new ArrayList<Ship>();
        Ship dummy = newShip(R.drawable.enemy_ship_right, 0, 0, "Red");
        int startingX = screenwidth/8;
        int startingRedY = screenheight - 50;
        int startingBlueY = panel.getHeight() + 20;
        int spaceBetweenShips = dummy.getMap().getWidth() * 3;
        for(int i = 0; i < numShipsInGame; i++){
            if(me.getShipColour().equals("blue")){
                me.getFleet().add(newShip(R.drawable.ship_right, startingX + i*spaceBetweenShips, startingBlueY, "Blue"));
                enemy.getFleet().add(newShip(R.drawable.enemy_ship_right, startingX + i * spaceBetweenShips, startingRedY, "Red"));
                Log.d("Ship", "Iteration " + i);
                continue;
            }
            else{
                me.getFleet().add(newShip(R.drawable.enemy_ship_right, startingX  + i*spaceBetweenShips, startingRedY, "Red"));
                enemy.getFleet().add(newShip(R.drawable.ship_right, startingX  + i*spaceBetweenShips, startingBlueY, "Blue"));
                Log.d("Ship", "Iteration " + i);
                continue;
            }

        }


    }

    /**
     *  Creates a sprite of a ship
     *
     * @param drawable --> R.drawable.id that is generated by Android
     * @return the Sprite of the ship
     */
    protected Ship newShip(int drawable, int startingX, int startingY, String color){
        //sets the image of the ship to the one specified
        map = BitmapFactory.decodeResource(getResources(), drawable);

        //Creates the new ship at the specified location

        Ship newShip = new Ship(this, map, startingX, startingY, 100,color, panel);
        numShipsCreated++;
        return newShip;
    }

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

        if(panel.isPaused()) {
            canvas.drawText("Resume", buttonLeftX + 5, (buttonBottomY - buttonTopY)/2+pixel_offset, buttonText);
        }
        else{
            canvas.drawText("Pause", buttonLeftX + 5, (buttonBottomY - buttonTopY)/2+pixel_offset, buttonText);
        }
    }


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
        renderPauseButton(canvas);

        Ship myShip;
        Ship enemyShip;

        //Abstract to a function and potentially in the wrong place (should be in ShipSprite)
        //If the ship bounces of an edge it changes direction
        Log.d("Num", "" + numShipsInGame);
        for(int i = 0 ; i < me.getFleet().size(); i++){
            Log.d("Ship", "Player Ship" + me.getFleet().get(i));
            Log.d("Ship", "Enemy Ship" + enemy.getFleet().get(i));
        }
        Log.d("Ship", "End of List\n\n");
        Log.d("Num", "P M F" + me.getFleet().size());
        Log.d("Num", "P E F" + enemy.getFleet().size());
        for(int i = 0; i < numShipsInGame; i++){

            if(i < me.getFleet().size()) {
                myShip = me.getFleet().get(i);
                //gets the image needed to be displayed based on the direction
                int resourceID = myShip.getDirectionID(myShip.getDirection());
                //sets the image of the ship to the specified image
                myShip.setMap(BitmapFactory.decodeResource(getResources(), resourceID));
                myShip.onDraw(canvas);
            }
            else {
                myShip = null;
            }
            if(i < enemy.getFleet().size()) {
                enemyShip = enemy.getFleet().get(i);
                int eID = enemyShip.getDirectionID(enemyShip.getDirection());
                enemyShip.setMap(BitmapFactory.decodeResource(getResources(), eID));
                enemyShip.onDraw(canvas);
            }
            else {
                enemyShip = null;
            }





            //draws the ship onto the canvas




            //This will eventually be looping through all GameObjects, not just ships
            /*for(int j = 0; j < enemy.getFleet().size(); j++) {

                Ship enemyShip = enemy.getFleet().get(j);
                int eID = enemyShip.getDirectionID(enemyShip.getDirection());
                enemyShip.setMap(BitmapFactory.decodeResource(getResources(), eID));
                enemyShip.onDraw(canvas);


                if(enemyShip.stillAlive()) {
                    myShip.detectCollision(enemyShip, v);
                } else{
                    enemy.removeShipFromFleet(enemyShip);
                }
            }*/
        }
    }


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

    public int getScreenXFromGridX(int gridX){
        double spaceBetweenGridPoints = (gridRight - gridLeft)/((double) numXGridPoints);
        return gridLeft + (int) (spaceBetweenGridPoints * gridX);
    }

    public int getScreenYFromGridY(int gridY){
        double spaceBetweenGridPoints = (gridBottom - gridTop)/((double) numYGridPoints);
        return gridTop + (int) (spaceBetweenGridPoints * gridY);
    }

    public int getMappedScreenX(int eventX){
        int gridX = getGridXFromScreenX(eventX);
        return getScreenXFromGridX(gridX);
    }

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

//onTouchEvent
    public boolean onTouchEvent(MotionEvent event) {
        String test = null;
        if (noClick) {
            test = "true";
        } else {
            test = "false";
        }
        Log.d("touchme", "noClick set to " + test);
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

                    shipReceivingInput.getxCoords().clear();
                    shipReceivingInput.getyCoords().clear();
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






}