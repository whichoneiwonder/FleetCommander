package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/*
*   Created by avnishjain and jmcma
*
*   Added NewGame button on MainActivity for debugging purposes.
*   Use this for testing game components when not using the P2P features.
*
* */
public class NewGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Requests the view not to show the top banner
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Sets the content of the custom view to be that of the Activity
        setContentView(new GameView(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
