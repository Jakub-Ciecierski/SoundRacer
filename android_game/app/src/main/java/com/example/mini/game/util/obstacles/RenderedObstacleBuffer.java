package com.example.mini.game.util.obstacles;

import com.example.mini.game.shapes.basic.Obstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.mathematics.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * One of 2 buffers used for obstacles.
 * It stores and translate obstacles which are drawn on the road.
 * <p></p>
 * Created by dybisz on 2014-12-01.
 */
public class RenderedObstacleBuffer {
    /**
     * Main list of obstacles which are currently being drawn on the screen.
     */
    private List<Obstacle> storedObstacles = new ArrayList<Obstacle>();
    /**
     * We randomize obstacles which are appearing on the scene and place them
     * on {@link com.example.mini.game.shapes.complex.Lattice lattice}'s cells
     * so maximal position which we can draw is number of vertical lines.
     */
    private int maxRandomNumber = (int) (Math.abs(GameBoard.LATTICE_BOTTOM_RIGHT_CORNER.getX()
            - GameBoard.LATTICE_WIDTH) / GameBoard.LATTICE_GAP_LENGTH);

    /**
     * Method generate integers from <0, {@link #maxRandomNumber}) interval.
     * Number of integers is specified by the argument.
     * In addition generation of numbers happens without repetitions.
     *
     * @param howManyYouWantToGet How many numbers you want to generate. Acceptable values
     *                            are from  0 to ({@link #maxRandomNumber} -1).
     * @return Array of randomly generated integersł.
     */
    private int[] randomizeNumbers(int howManyYouWantToGet) {
        if (howManyYouWantToGet >= 0 && howManyYouWantToGet < maxRandomNumber) {
            List<Integer> listOfRandomIntegers = new ArrayList<Integer>();
            int[] returnArray = new int[howManyYouWantToGet];
            for (int i = 0; i < maxRandomNumber; i++) {
                listOfRandomIntegers.add(i);
            }
            Collections.shuffle(listOfRandomIntegers);
            /* ArrayList<Integer> to int[] conversion */
            for (int i = 0; i < howManyYouWantToGet; i++) {
                returnArray[i] = listOfRandomIntegers.get(i);
            }
            return returnArray;
        } else
            return null;
    }

    /**
     * Passed obstacles are added to the buffer.
     * Start position of each is randomized based on {@link #randomizeNumbers(int)} method.
     *
     * @param obstacles A list of obstacles you want to add to the buffer.
     */
    public void addToBuffer(List<Obstacle> obstacles) {
        /* Each obstacle from list passed in arguments receives
           unique number from */
        int[] randomNumbers = randomizeNumbers(obstacles.size());
        for (int i = 0; i < obstacles.size(); i++) {
            /* Obstacle starts at the end of road */
            obstacles.get(i).setAssignedVertexIndex(GameBoard.ROAD_VERTICES_PER_BORDER);

            /* Randomness of obstacle position is provided by following coefficient.
               We calculate it by figuring out a ration between generated number
               and the maximum possible value for generated numbers */
            obstacles.get(i).setAssignedVertexVectorCoefficient((float) randomNumbers[i] /
                    (float) maxRandomNumber);

            /* We calculate vector parallel to vector between current vertex hooked to
               the obstacle and a corresponding outer vertex of length appropriate
               to randomly generated number */
            float[] temp = Road.getVertices(obstacles.get(i).getAssignedVertexIndex());
            float[] innerOuterVector = new float[]
                    {
                            obstacles.get(i).getAssignedVertexVectorCoefficient() * (temp[3] - temp[0]),
                            obstacles.get(i).getAssignedVertexVectorCoefficient() * (temp[4] - temp[1]),
                            obstacles.get(i).getAssignedVertexVectorCoefficient() * (temp[5] - temp[2]),
                    };

            /* Translate obstacle to proper start position */
            obstacles.get(i).setTranslationCoordinates(new Vector3
                    (
                            innerOuterVector[0] + temp[0],
                            innerOuterVector[1] + temp[1],
                            innerOuterVector[2] + temp[2]
                    ));

            /* Store the object for further usage */
            storedObstacles.add(obstacles.get(i));
        }
    }

    /**
     * Checks which obstacles passed vanishBorder or are hooked to
     * first vertex of the road, delete them from the stored obstacles
     * and return.
     *
     * @param vanishBorder Number which suggests lowest possible value
     *                     for Z coordinate for each obstacle. After
     *                     crossing this 'border' the object will be
     *                     qualify as free.
     * @return A list of 'free' objects. Objects which are not supposed
     * to be drawn.
     */
    public ArrayList<Obstacle> getFreeObstacles(float vanishBorder) {
        ArrayList<Obstacle> freeObstacles =
                new ArrayList<Obstacle>();
        for (Obstacle obstacle : storedObstacles) {
            if (obstacle.getTranslationZ() <= vanishBorder ||
                    obstacle.getAssignedVertexIndex() == 1) {
                freeObstacles.add(obstacle);
            }
        }
        storedObstacles.removeAll(freeObstacles);
        return freeObstacles;
    }

    /**
     * Each animation frame this method recalculates position of the obstacle
     * according to vertesx it is hooked to.
     */
    public void render() {
        for (Obstacle o : storedObstacles) {
            o.decrementAssignedVertex();
            /* Calculate new position for fresh vertex */
            float[] temp = Road.getVertices(o.getAssignedVertexIndex());
            float[] innerOuterVector = new float[]
                    {
                            o.getAssignedVertexVectorCoefficient() * (temp[3] - temp[0]),
                            o.getAssignedVertexVectorCoefficient() * (temp[4] - temp[1]),
                            o.getAssignedVertexVectorCoefficient() * (temp[5] - temp[2]),
                    };
            /* Apply translation to the obstacle */
            o.setTranslation(new Vector3(
                    innerOuterVector[0] + temp[0],
                    innerOuterVector[1] + temp[1],
                    innerOuterVector[2] + temp[2]));
        }
    }
}
