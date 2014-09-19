package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by avnishjain on 19/09/14.
 */
public class ShipSprite {

    private GameView gameView;
    private Bitmap map;
    private int xSpeed;
    private int xPosition;
    private int yPosition;
    public final static int pixelOffset = 5;

    public ShipSprite(GameView gameView, Bitmap map, int xPosition, int yPosition){
        this.gameView = gameView;
        this.map = map;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    private void update(){
        Log.i("Debugging", "Ship Position : (" + xPosition + ", " + yPosition + ")\n" );
        Log.i("Debugging", "Edge of Screen : " + (gameView.getWidth() - map.getWidth()) + "\n\n");

        if (xPosition == gameView.getWidth() - map.getWidth() - pixelOffset) {
            xSpeed = -1 ;
        }
        if (xPosition == pixelOffset) {
            xSpeed = 1;
        }

        xPosition = xPosition + xSpeed;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(map, xPosition , yPosition, null);
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

    public int getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }
}
