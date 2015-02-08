package com.example.mini.game.shapes.basic;


/**
 * Created by user on 2015-02-05.
 */
public class RoadVertex {
    public float x;
    public float y;
    public float z;
    public boolean obstacleIsAssigned;

    public RoadVertex(float x, float y, float z, boolean obstacleIsAssigned) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.obstacleIsAssigned = obstacleIsAssigned;

    }
}
