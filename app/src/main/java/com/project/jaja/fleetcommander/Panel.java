package com.project.jaja.fleetcommander;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by avnishjain on 15/10/14.
 */
public class Panel implements com.project.jaja.fleetcommander.Observable {

        private GameView gameView;
        private Paint paint;
        private Paint scorePaint;

        private int playerRenderedScore;
        private int playerScore;
        private int enemyRenderedScore;
        private int enemyScore;

        private String playerRenderedScoreString;
        private String enemyRenderedScoreString;

        private String renderedScoreString;

        private int leftX;
        private int topY;
        private int rightX;
        private int bottomY;

        private boolean isPaused;

        private ArrayList<Observer> observers = new ArrayList<Observer>();

        public Panel(GameView gameView){
            this.gameView = gameView;

            leftX = 0;
            topY = 0;
            rightX = gameView.screenwidth;
            bottomY = gameView.screenheight/10;

            renderedScoreString = null;
            enemyRenderedScoreString = null;
            playerRenderedScoreString = null;

            playerScore = 0;
            playerRenderedScore = 10;
            enemyRenderedScore = 10;
            enemyScore = 30;

            isPaused = false;


        }

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


