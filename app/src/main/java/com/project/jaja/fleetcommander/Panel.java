package com.project.jaja.fleetcommander;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by avnishjain on 15/10/14.
 */
public class Panel {

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

    public Panel(GameView gameView){
        this.gameView = gameView;

        leftX = 0;
        topY = 0;
        rightX = gameView.screenwidth;
        bottomY = 80;

        renderedScoreString = null;
        enemyRenderedScoreString = null;
        playerRenderedScoreString = null;

        playerScore = 0;
        playerRenderedScore = 10;
        enemyRenderedScore = 10;
        enemyScore = 30;



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
        canvas.drawRect(0, 0, rightX,80, paint);

        canvas.drawText(renderedScoreString, rightX/2 - 50, bottomY/2 + 20, scorePaint);

    }

    public int getHeight() {
        return bottomY;
    }


}
