package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.project.jaja.fleetcommander.util.SystemUiHider;

import org.json.JSONException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    // The statistics for all the games this Player has played
    private Statistics stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (intent != null) {
            String jsonData = intent.getStringExtra("stats");
            try {
                stats = new Statistics(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            stats = new Statistics();
        }
    }


    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };


    public void goToP2P(View view) {
        Intent intent = new Intent(getApplicationContext(), P2PActivity.class);

        try {
            intent.putExtra("stats", stats.toJSONString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startActivity(intent);
    }

    public void goToNewGame(View view) {
        Intent intent = new Intent(getApplicationContext(), NewGameActivity.class);
        startActivity(intent);
    }
}
