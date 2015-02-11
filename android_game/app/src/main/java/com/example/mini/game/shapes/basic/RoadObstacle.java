package com.example.mini.game.shapes.basic;

/**
 * As we have changed the way in which obstacle are treated
 * (now they are "a part of the road"), new class with desired
 * information is needed. Here it is. It is fully suitable for
 * filling obstacles array in {@link com.example.mini.game.shapes.complex.Road}
 * class.
 * <p></p>
 * Created by dybisz on 2015-02-07.
 */
public class RoadObstacle {
    private float[] translation;

    public RoadObstacle(float x, float y, float z) {
        translation = new float[]{x, y, z};

    }
    public float[] getTranslation() {
        return translation;
    }
}
