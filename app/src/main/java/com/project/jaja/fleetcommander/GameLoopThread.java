package com.project.jaja.fleetcommander;

import android.graphics.Canvas;

/**
 * Created by avnishjain on 19/09/14.
 */
public class GameLoopThread extends Thread {

    public static final long FPS = 100;
    private GameView view;
    private boolean isGameStateActive = false;

    public GameLoopThread(GameView view){
        this.view = view;
    }

    public void setGameState(boolean gameState){
        isGameStateActive = gameState;
    }

    public boolean isGameStateActive(){
        return isGameStateActive;
    }

    @Override
    public void run(){

        long ticksPerSecond = 1000 / FPS;
        long startTime, sleepTime;

        while(this.isGameStateActive()){
            Canvas canvas = null;
            startTime = System.currentTimeMillis();

            try{
                canvas = view.getHolder().lockCanvas();
                synchronized (view.getHolder()){
                    view.onDraw(canvas);
                }
            }
            finally{
                if(canvas != null){
                    view.getHolder().unlockCanvasAndPost(canvas);
                }
            }

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

}
