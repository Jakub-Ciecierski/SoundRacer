package com.example.mini.game.util.obstacles;

import android.util.Log;

import com.example.mini.game.shapes.basic.Obstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.util.mathematics.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Created by user on 2014-12-01.
 */
public class RenderedObstacleBuffer {
    private List<Obstacle> storedObstacles = new ArrayList<Obstacle>();
    /**
     * We randomize obstacles which are appearing on the scene and place them
     * on {@link pl.dybisz.testgry.shapes.complex.Lattice lattice}'s cells
     * so maximal position which we can draw is number of vertical lines.
     */
    private int maxRandomNumber = (int) (Math.abs(GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getX()
            - GameBoard.LATTICE_WIDTH) / GameBoard.LATTICE_GAP_LENGTH);

    private int[] randomizeNumbers(int howManyYouWantToGet) {
        List<Integer> randomList = new ArrayList<Integer>();
        int[] returnList = new int[howManyYouWantToGet];
        for (int i = 0; i < maxRandomNumber; i++) {
            randomList.add(i);
        }
        Collections.shuffle(randomList);
        for (int i = 0; i < howManyYouWantToGet; i++) {
            returnList[i] = randomList.get(i);
        }

        return returnList;
    }

    public void addToBuffer(List<Obstacle> obstacles) {
        int[] randomNumbers = randomizeNumbers(obstacles.size());
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).setTranslationCoordinates(new Vector3(randomNumbers[i]
                    * GameBoard.LATTICE_GAP_LENGTH, 0f, GameBoard.LATTICE_LENGTH));
            storedObstacles.add(obstacles.get(i));
        }

    }

    // moze nie byc potrzebne
    public boolean areThereAnyFreeObstacles() {
        for (Obstacle obstacle : storedObstacles) {
            if (obstacle.getTranslationZ() <= 0)
                return true;
        }
        return false;
    }

    public ArrayList<Obstacle> getFreeObstacles(float vanishBorder) {
        ArrayList<Obstacle> returnArrayList =
                new ArrayList<Obstacle>();
        for (Obstacle obstacle : storedObstacles) {
            if (obstacle.getTranslationZ() <= vanishBorder) {
                returnArrayList.add(obstacle);
            }
        }
        storedObstacles.removeAll(returnArrayList);
        ///Log.i("RENDERED_OBSTACLE_BUFFER", "returned free obstacles: " + returnArrayList.size());
        return returnArrayList;
    }


    public Obstacle[] getObstacles(int numberOfObstacles) {
        Obstacle[] returnList = new Obstacle[numberOfObstacles];
        for (int i = 0; i < numberOfObstacles; i++) {
            returnList[i] = storedObstacles.get(storedObstacles.size() - 1);
            storedObstacles.remove(storedObstacles.size() - 1);
        }
        return returnList;
    }

    public void render() {
        for (Obstacle o : storedObstacles) {
            o.setTranslationZ(o.getTranslationZ() - GameBoard.TIME_UNIT_LENGTH);
        }
    }
}
