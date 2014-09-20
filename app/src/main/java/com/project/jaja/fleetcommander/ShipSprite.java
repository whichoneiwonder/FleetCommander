package com.project.jaja.fleetcommander;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by avnishjain on 19/09/14.
 */
public class ShipSprite {

    private GameView gameView;
    private Bitmap map;
    private int xSpeed;
    private int ySpeed;
    private int xPosition;
    private int yPosition;
    public final static int pixelOffset = 5;

    public ShipSprite(GameView gameView, Bitmap map, int xPosition, int yPosition){
        this.gameView = gameView;
        this.map = map;
        //this.xPosition = (int)(Math.random() * (gameView.getWidth() - map.getWidth())) + 1;
        //this.xPosition = (int)(Math.random() * (gameView.getHeight() - map.getHeight())) + 1;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.xSpeed = (int) (Math.random()*10)+1;
        this.ySpeed = (int) (Math.random()*10)+1;
        //this.xSpeed = 1;
        //this.ySpeed = 1;


    }

    private void update(){
        //Log.i("Debugging", "Ship Position : (" + xPosition + ", " + yPosition + ")\n" );
        //Log.i("Debugging", "Edge of Screen : " + (gameView.getWidth() - map.getWidth()) + "\n\n");

        if (xPosition > gameView.getWidth() - map.getWidth() - xSpeed || xPosition + xSpeed < 0) {
            xSpeed = -xSpeed ;
        }

        if (yPosition > gameView.getHeight() - map.getHeight() - ySpeed || yPosition + ySpeed < 0) {
            ySpeed = -ySpeed ;
        }

        xPosition = xPosition + xSpeed;
        yPosition = yPosition + ySpeed;

    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(map, xPosition , yPosition, null);
    }

    public boolean isColliding (float x, float y) {
        return x > xPosition && x < xPosition + map.getWidth() && y > yPosition && y < yPosition + map.getHeight();
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public void setXSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public void setYSpeed(int ySpeed) {
        this.ySpeed = ySpeed;
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
}
