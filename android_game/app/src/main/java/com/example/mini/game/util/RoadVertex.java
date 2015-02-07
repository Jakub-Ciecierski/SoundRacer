package com.example.mini.game.util;

import android.util.Log;

import com.example.mini.game.shapes.basic.NowyKurwaObstacle;
import com.example.mini.game.shapes.basic.Obstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.mathematics.Vector3;
import com.example.mini.game.util.obstacles.ChujTamRenderujOdbyty;

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
            //obstacle = new Obstacle(1.0f, 1.0f, 1.0f, GameBoard.OBSTACLES_COLOR);
            //obstacle.setTranslationZ(z);
            //obstacle.setTranslation(new Vector3(3.0f, y,z));

//            ChujTamRenderujOdbyty.obstacles.add(obstacle);
            Road.obstacle.add(new NowyKurwaObstacle(x,y,z));
            Log.i("TUTAJ", "TUTAJ CHUJKU");
        }

    }
}
