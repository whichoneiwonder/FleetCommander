package com.project.jaja.fleetcommander;

import android.graphics.Canvas;

/**
 * Created by avnishjain on 19/09/14.
 */
public class GameLoopThread extends Thread {

    //Constant defining the number of Frames Per Second
    public static final long FPS = 100;

    //The view the game will be rendered upon
    private GameView view;

    //Boolean that describes whether the game has entered active state
    private boolean isGameStateActive = false;

    /** Constructor */
    public GameLoopThread(GameView view){
        this.view = view;
    }

    @Override
    public void run(){

        //Variables for stabilising the frame rate
        long ticksPerSecond = 1000 / FPS;
        long startTime, sleepTime;

        while(this.isGameStateActive()){

            //Canvas to draw to
            Canvas canvas = null;
            startTime = System.currentTimeMillis();

            //Initialises the canvas and starts the rendering of the view
            try{
                canvas = view.getHolder().lockCanvas();
                synchronized (view.getHolder()){
                    view.onGameViewDraw(canvas);
                }
            }

            //If an exception is thrown during normal gameplay,
            // the view is 'unlocked' if the canvas exists
            finally{
                if(canvas != null){
                    view.getHolder().unlockCanvasAndPost(canvas);
                }
            }

            //If the time it took to render that frame was less than 10 ms,
            // sleep for the remainder of the frame time, else sleep for 10 ms
            sleepTime = ticksPerSecond - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            }
            catch (Exception e) {
                /* do nothing */
            }
        } // end while loop
    }

    //=============================================================================================
    //                          ACCESSOR AND MUTATOR METHODS
    //=============================================================================================

    /**
     *  @param gameState a boolean value representing whether the game is active or not
     *  Mutator method for the isGameStateActive variable
     */
    public void setGameState(boolean gameState){
        isGameStateActive = gameState;
    }

    /**
     * @return isGameStateActive a boolean value representing whether or not the game
     * is active
     *  Accessor method for the isGameStateActive variable
     */
    public boolean isGameStateActive(){
        return isGameStateActive;
    }

    /** Overriden method of the superclass Thread
     *
     * Acts as the infinite game loop. Mirrors the update method
     * in the Slick main class.
     *
     * */

}
