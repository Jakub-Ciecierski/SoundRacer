package com.example.mini.game.util;

import com.example.mini.game.shapes.basic.Obstacle;

/**
 * Created by user on 2015-02-05.
 */
public class RoadVertex {
    public float x;
    public float y;
    public float z;
    public Obstacle obstacle = null;
    public float obstaclePosition;

    public RoadVertex(float x, float y, float z, boolean obstacleIsAssigned) {
        this.x = x;
        this.y = y;
        this.z = z;
        if(obstacleIsAssigned){
            obstacle = new Obstacle(1.0f, 1.0f, 1.0f, new float[]{1.0f,0.0f,1.0f});
            obstacle.setTranslationZ(z);
        }

    }
}
