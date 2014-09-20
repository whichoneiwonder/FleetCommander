package com.project.jaja.fleetcommander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView {

    private Bitmap map;

    private SurfaceHolder surfaceHolder;
    private GameLoopThread thread;
    private ArrayList<ShipSprite> ships;
    private int numShips;
    private long timeTilllastClick;

    public GameView(Context context){
        super(context);
        thread = new GameLoopThread(this);
        ships = new ArrayList<ShipSprite>();
        numShips = 0;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                populateShips();
                thread.setGameState(true);
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                boolean retry = true;
                thread.setGameState(false);
                while (retry) {
                    try {
                        thread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }


            }
        });


    }

    protected void populateShips(){
        ships.add(newShip(R.drawable.player_ship));
        ships.add(newShip(R.drawable.player_ship));
        ships.add(newShip(R.drawable.player_ship));
    }

    protected ShipSprite newShip(int drawable){
        map = BitmapFactory.decodeResource(getResources(), drawable);

        ShipSprite newShip = new ShipSprite(this, map, (numShips * 110)+30, 10);
        numShips++;
        return newShip;
    }

    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.rgb(0,153,204));

        for(ShipSprite ship : ships){
            ship.onDraw(canvas);
        }

    }

    public boolean isShipColliding(){
        for(int i = 0; i < ships.size(); i++){
            for(int j = 0; j < ships.size(); j++){
                if (i != j && checkCollision(ships.get(i), ships.get(j))){
                    Log.i("Collision", "Collision between ships");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkCollision(ShipSprite shipOne, ShipSprite shipTwo){
        if(shipOne.isColliding(shipTwo.getXPosition(),shipTwo.getYPosition())
                || shipOne.isColliding(shipTwo.getXPosition() + map.getWidth(), shipTwo.getYPosition())
                || shipOne.isColliding(shipTwo.getXPosition(), shipTwo.getYPosition() + map.getHeight())
                || shipOne.isColliding(shipTwo.getXPosition() + map.getWidth(), shipTwo.getYPosition() + map.getWidth())
                ){
                    return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - timeTilllastClick > 500) {
            timeTilllastClick = System.currentTimeMillis();
            synchronized (getHolder()) {
               for(ShipSprite ship: ships){
                    if (ship.isColliding(event.getX(), event.getY())) {
                        //Do something with the ships here
                        ships.remove(ship);
                        break;
                    }
                }
            }
        }
        return true;
    }


}
