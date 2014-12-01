package pl.dybisz.testgry.util.obstacles;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.dybisz.testgry.shapes.basic.Obstacle;
import pl.dybisz.testgry.util.mathematics.Vector3;

/**
 * Created by user on 2014-12-01.
 */
public class AvailableObstacleBuffer {
    private final static Vector3 DEFAULT_STORING_COORDINATES = new Vector3(30f, 1f, -11f);
    private List<Obstacle> storedObstacles = new ArrayList<Obstacle>();

    public AvailableObstacleBuffer() {

    }

    public AvailableObstacleBuffer(Obstacle[] obstacles) {
        addToBuffer(obstacles);
    }

    public void addToBuffer(Obstacle obstacle) {
        storedObstacles.add(obstacle);
        obstacle.setTranslationCoordinates(DEFAULT_STORING_COORDINATES);
    }

    public void addToBuffer(Obstacle[] obstacles) {
        for (Obstacle obstacle : storedObstacles) {
            obstacle.setTranslationCoordinates(DEFAULT_STORING_COORDINATES);
        }
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i].setTranslationCoordinates(DEFAULT_STORING_COORDINATES);
            storedObstacles.add(obstacles[i]);
        }

    }

    public void addToBuffer(List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            obstacle.setTranslationCoordinates(DEFAULT_STORING_COORDINATES);
        }
        if (obstacles.size() > 0)
            storedObstacles.addAll(obstacles);
    }

    public boolean areThereObstacles(int numberOfObstacles) {
        return (storedObstacles.size() >= numberOfObstacles) ?
                true : false;
    }

    public List<Obstacle> getObstacles(int numberOfObstacles) {
        List<Obstacle> returnList = new ArrayList<Obstacle>();
        if (areThereObstacles(numberOfObstacles)) {
            for (int i = 0; i < numberOfObstacles; i++) {
                returnList.add(storedObstacles.remove(storedObstacles.size() - 1));
            }
        }
        return returnList;
    }

    public void print(String tag) {
        Log.i(tag, "" + storedObstacles.size());
    }
}
