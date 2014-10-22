package com.project.jaja.fleetcommander;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * The class actually responsible for match statistics of the player
 */
public class StatisticsActivity extends Activity {


    /**
     * Converts the read match statistics into an HTML output that is easily human readable
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);

        String data = readTxt();
        Statistics stats = null;

        try {
            stats = new Statistics(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // The following is a quick and easy statistics interface focused around the actual
        // features and not too much on the UI. This is a core feature but not in terms of UI
        TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);

        String[] words = new String[3];
        words[0] = "<h2>Statistics<h2>";
        words[1] = "<i>There is a list of MAC Addresses of opponents, followed by</i>";
        words[2] = "<i>the matches you played together, the date, time and score</i>";

        for (int i = 0; i < 3; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView tv = new TextView(this);
            tv.setText(Html.fromHtml(words[i]));

            row.addView(tv);
            tl.addView(row, i);
        }

        Map<String, ArrayList<Statistic>> statsMap = stats.getStats();

        for (String mac: statsMap.keySet()) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView tv1 = new TextView(this);
            tv1.setText(Html.fromHtml("<b>" + mac + "</b>"));
            row.addView(tv1);
            tl.addView(row);

            ArrayList<Statistic> statsList = statsMap.get(mac);

            for (Statistic stat: statsList) {
                TableRow row2 = new TableRow(this);
                row2.setLayoutParams(lp);
                TextView tv2 = new TextView(this);
                tv2.setText(stat.getDateTime());

                row2.addView(tv2);
                tl.addView(row2);

                TableRow row3 = new TableRow(this);
                row3.setLayoutParams(lp);
                TextView tv3 = new TextView(this);
                tv3.setText(Html.fromHtml(stat.winOrLose()));

                row3.addView(tv3);
                tl.addView(row3);
            }
        }
    }

    /**
     * Reading the statistics in from the text file on the device
     * @return A string containing all match results for the player
     */
    public String readTxt() {
        SharedPreferences settings = getSharedPreferences("fleetCommander", 0);
        return settings.getString("playerStatistics", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistics, menu);
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