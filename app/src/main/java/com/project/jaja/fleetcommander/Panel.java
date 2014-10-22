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

        //The player's score that is currently on the panel
        private int playerRenderedScore;

        //The player's actual score
        private int playerScore;

        //The enemy's score that is on the panel
        private int enemyRenderedScore;

        //The enemy's actual score
        private int enemyScore;

        //Strings of both the player's and enemy's rendered score
        private String playerRenderedScoreString;
        private String enemyRenderedScoreString;

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

            //As the player's don't have a score yet we can set this to null
            renderedScoreString = null;
            enemyRenderedScoreString = null;
            playerRenderedScoreString = null;

            //As the game has just started no one has any points
            playerScore = 0;
            playerRenderedScore = 0;
            enemyRenderedScore = 0;
            enemyScore = 0;

            //The game starts in an unpaused state
            isPaused = false;


        }

        /**
         *An update method for the panel. To reduce overheard with drawing, we determine which values
         * have changed and only update these values. This method also gets called when the game is
         * paused
         */
        public void update(){

            if (this.playerScore != this.playerRenderedScore || playerRenderedScoreString == null) {
                this.playerRenderedScore = this.playerScore;
                this.playerRenderedScoreString = Integer.toString(this.playerRenderedScore);
            }
            if(enemyScore != this.enemyRenderedScore || enemyRenderedScoreString == null){
                this.enemyRenderedScore = this.enemyScore;
                this.enemyRenderedScoreString = Integer.toString(this.enemyRenderedScore);
            }

            renderedScoreString = playerRenderedScoreString + " : " + enemyRenderedScoreString;

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


