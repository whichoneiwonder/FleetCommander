package com.project.jaja.fleetcommander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView {

    private Bitmap map;
    private SurfaceHolder surfaceHolder;
    private GameLoopThread thread;
    private ShipSprite ship;

    public GameView(Context context){
        super(context);
        thread = new GameLoopThread(this);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
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

        map = BitmapFactory.decodeResource(getResources(), R.drawable.player_ship);
        ship = new ShipSprite(this, map, 5, 10);
    }

    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.rgb(0,153,204));
        ship.onDraw(canvas);

    }

}
