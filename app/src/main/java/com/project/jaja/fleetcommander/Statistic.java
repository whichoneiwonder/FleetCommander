package com.project.jaja.fleetcommander;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by anty on 18/10/14.
 * Simple class to manage statistics for each game
 *
 */
public class Statistic {

    // This Player's score
    private int myScore;

    // Opponent's score
    private int opponentScore;

    // The time at the end of the match
    private String dateTime;

    /**
     * Constructs a Statistic with the final score of each Player and the current time
     * @param myScore The score of this phone
     * @param opponentScore The score on the opponent's
     */
    public Statistic(int myScore, int opponentScore) {
        this.myScore = myScore;
        this.opponentScore = opponentScore;
        this.dateTime = DateFormat.getDateTimeInstance().format(new Date());
    }

    public Statistic(int myScore, int opponentScore, String dateTime) {
        this.myScore = myScore;
        this.opponentScore = opponentScore;
        this.dateTime = dateTime;
    }

    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
