package com.project.jaja.fleetcommander;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by avnishjain on 15/10/14.
 */
public class Panel implements com.project.jaja.fleetcommander.Observable {

    //The GameView in which this Panel Exists
    private GameView gameView;

    //The paint for the Panel
    private Paint paint;

    //The paint for the scores
    private Paint scorePaint;

    // This Player's score
    private String myScore = "0";

    //The enemy's actual score
    private String enemyScore = "0";

    private String timeLeft = "10";

    //A combination of the above two strings
    private String renderedScoreString;

    //Integers representing the coordinates which the panel is rendered with respect to
    private int leftX;
    private int topY;
    private int rightX;
    private int bottomY;

    //A boolean that keeps track of whether or not the game is paused
    private boolean isPaused;

    //An array list that keeps track of the observers observing the panel
    private ArrayList<Observer> observers = new ArrayList<Observer>();

    /**
     * The standard constructor for a Panel
     * @param gameView the GameView in which the panel is being rendered
     */
    public Panel(GameView gameView){
        this.gameView = gameView;

        //We want to render the panel starting in the top-left corner
        leftX = 0;
        topY = 0;

        //We also want the panel to occupy 10% of the screen
        rightX = gameView.screenwidth;
        bottomY = gameView.screenheight/10;

        //The game starts in an unpaused state
        isPaused = false;
    }

    public void setMyScore(int score) {
        myScore = Integer.toString(score);
    }

    public void setEnemyScore(int score) {
        enemyScore = Integer.toString(score);
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = Long.toString(timeLeft / 1000);
    }

    /**
     *An update method for the panel. To reduce overheard with drawing, we determine which values
     * have changed and only update these values. This method also gets called when the game is
     * paused
     */
    public void update() {
        renderedScoreString = myScore + " : " + enemyScore;

        if(isPaused){
            renderedScoreString = "PAUSED";
        }

    }


    /**
     * The method we use to draw the canvas
     * @param canvas the Canvas on which the panel is being drawn
     */
    public void onDraw(Canvas canvas) {
        update();

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(0);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setStrokeWidth(4);
        scorePaint.setTextSize(50);
        canvas.drawRect(0, 0, rightX,bottomY, paint);

        canvas.drawText(renderedScoreString, rightX/2 - ((int) (bottomY*0.75)), bottomY/2 + 20, scorePaint);
        canvas.drawText(timeLeft, rightX - (rightX/4), bottomY/2 + 20, scorePaint);
    }

    //=============================================================================================
    //                          ACCESSOR AND MUTATOR METHODS
    //=============================================================================================

    public int getHeight() {
        return bottomY;
    }
    public int getWidth() {
        return rightX;
    }

    public void setPause(boolean pause) {isPaused = pause;}

    public void clickPause() {
        notifyObservers();
        setPause(!isPaused);
    }

    public boolean isPaused(){return isPaused;}

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        for(Observer o: observers){
            o.update(this);
        }
    }
}


