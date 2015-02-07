package com.example.mini.game.util;

<<<<<<< remotes/origin/dla_dybcia
import com.example.mini.game.shapes.basic.Obstacle;
=======
import android.util.Log;
import com.example.mini.game.shapes.basic.Obstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.mathematics.Vector3;

>>>>>>> local

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
<<<<<<< remotes/origin/dla_dybcia
            obstacle = new Obstacle(1.0f, 1.0f, 1.0f, new float[]{1.0f,0.0f,1.0f});
            obstacle.setTranslationZ(z);
=======

>>>>>>> local
        }

    }
}
