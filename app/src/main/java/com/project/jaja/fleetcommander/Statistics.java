package com.project.jaja.fleetcommander;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class handles the game statistics that will be stored
 */
public class Statistics {
    // Statistic HashMap with the key being a MAC Address
    private Map<String, ArrayList<Statistic>> stats = new HashMap<String, ArrayList<Statistic>>();

    /**
     * Constructs an empty HashMap. This is only executed for a brand new instance of the app
     * where no games have been played at all. Typically, there will be some amount of data already
     * on the phone
     */
    public Statistics() {}

    /**
     * Constructs a Statistics HashMap using JSON data. This is so that data passed between
     * intents and being written/read to file can easily be interpreted and this gives us the
     * opportunity for there to be extension to other platforms such as iOS with ease
     * @param jsonData JSON data as a String
     * @throws JSONException
     */
    public Statistics(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);

        Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String mac = keys.next();
            JSONArray statsListJSON = data.getJSONArray(mac);
            ArrayList<Statistic> statsList = new ArrayList<Statistic>();

            for (int i = 0; i < statsListJSON.length(); i++) {
                JSONObject entry = statsListJSON.getJSONObject(i);

                int myScore = entry.getInt("myScore");
                int opponentScore = entry.getInt("opponentScore");
                String dateTime = entry.getString("dateTime");

                Statistic stat = new Statistic(myScore, opponentScore, dateTime);

                statsList.add(stat);
            }

            stats.put(mac, statsList);
        }
    }

    /**
     * Adds a statistic to the HashMap
     * @param mac MAC Address
     * @param stat Statistic
     */
    public void addStatistics(String mac, Statistic stat) {
        ArrayList<Statistic> statsList;

        if (stats.containsKey(mac)) {
            statsList = stats.get(mac);
        } else {
            statsList = new ArrayList<Statistic>();
        }
        statsList.add(stat);
        stats.put(mac, statsList);
    }

    public ArrayList<Statistic> getPlayerStatistics(String macAddress) {
        return stats.get(macAddress);
    }

    public Map<String, ArrayList<Statistic>> getStats() {
        return stats;
    }

    /**
     * Builds a JSON Object using the HashMap and then returns this data as a String
     * @return JSON data as a String
     * @throws JSONException
     */
    public String toJSONString() throws JSONException {
        JSONObject data = new JSONObject();

        // For each MAC Address
        for (String key : stats.keySet()) {
            ArrayList<Statistic> statsList = stats.get(key);
            JSONArray keyArray = new JSONArray();

            // For each Statistic associated with that MAC Address
            for (Statistic stat : statsList) {
                JSONObject statJSON = new JSONObject();
                statJSON.put("dateTime", stat.getDateTime());
                statJSON.put("opponentScore", stat.getOpponentScore());
                statJSON.put("myScore", stat.getMyScore());
                keyArray.put(statJSON);
            }
            data.put(key, keyArray);
        }
        return data.toString();
    }
}
