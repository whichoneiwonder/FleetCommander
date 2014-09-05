package com.project.jaja.fleetcommander.code;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anty on 5/09/14.
 */
public class Move {
    private int maxSteps;
    private int turn;
    private Location initial;
    private int[] moves;
    Map<Integer, Integer> shipDirection = new HashMap<Integer, Integer>() {
        {
            put(0, 1);
            put(1, 1);
            put(2, 2);
            put(3, 3);
            put(4, 3);
            put(5, 3);
            put(6, 2);
            put(7, 1);
        }
    };

    public Move(int maxSteps, int turn, Location initial, int[] moves) {
        this.maxSteps = maxSteps;
        this.turn = turn;
        this.initial = initial;
        this.moves = moves;
    }
    /*
     * Each planning phase, users are allotted 10 steps.
     *
     * Depending on the direction in which you go, you will be 'penalised' for making steps that
     * are out of the ordinary. For example, ships may make u-turns, however this will take up 3
     * steps. Arrow indicates the direction the ship is facing (north):
     *
     *              1 | 1 | 1
     *              ---------
     *              2 | ^ | 2
     *              ---------
     *              3 | 3 | 3
     *
     * Ship direction is adjusted in the function below and then steps are calculated using the
     * above mapping
     */
    public int calculateStepsTaken(int initialDir, int finalDir) {
        return shipDirection.get(adjustment(initialDir, finalDir));
    }

    /*
     * Adjusts the directions relative to north. This means the
     */
    public int adjustment(int initialDir, int finalDir) {
        int adjustedDir;
        if (finalDir - initialDir < 0) {
            adjustedDir = 8 + finalDir - initialDir;
        } else {
            adjustedDir = finalDir - initialDir;
        }
        return adjustedDir;
    }

    /*
	 * A player can move each turn in 8 directions and this function helps
	 * calculate the new location given an initial location and a direction
	 * in which the ship has moved.
	 *
	 * Directions are specified as follows (star denotes the original location):
	 *
	 *				7 | 0 | 1
	 *				----------
	 *				6 | * | 2
	 *				----------
	 *				5 | 4 | 3
	 *
	 * As moves made are on the board and can only be recorded within the grid,
	 * there is no reason to do boundary checks.
	 */
    public Location calculateNewLocation(Location initial, int initialDir, int finalDir) {
        Location next = new Location(initial.getX(), initial.getY());

            switch (finalDir) {
                case 0:
                    next.moveY(1);
                    break;

                case 1:
                    next.moveY(1);
                    next.moveX(1);
                    break;

                case 2:
                    next.moveX(1);
                    break;

                case 3:
                    next.moveX(1);
                    next.moveY(-1);
                    break;

                case 4:
                    next.moveY(-1);
                    break;

                case 5:
                    next.moveX(-1);
                    next.moveY(-1);
                    break;

                case 6:
                    next.moveX(-1);
                    break;

                case 7:
                    next.moveX(-1);
                    next.moveY(1);
                    break;

                default:
                    System.out.println("invalid direction");
                    System.exit(1);
                    break;
            }

        return next;
    }
}
