package com.example.mini.game.shapes.complex;

import com.example.mini.game.shapes.basic.Cube;

/**
 * Created by dybisz on 2014-12-08.
 */
public class Player extends Cube {
    public static void setTranslate(float x, float y, float z) {
        if (x <= GameBoard.ROAD_WIDTH - GameBoard.PLAYER_WIDTH && x >= 0 + GameBoard.PLAYER_WIDTH)
            translate[0] = x;
        translate[1] = y;
        translate[2] = z;
    }
    public static void rotateAroundZ(float a) {
        if(a <= 35 && a >= -35)
            rotate = new float[] {a,0.0f,0.0f,1.0f};
    }

    public static float getCurrentAngle() {
        return rotate[0];
    }
    public static float getTranslationX() {
        return translate[0];
    }

    public static float getTranslationY() {
        return translate[1];
    }

    public static float getTranslationZ() {
        return translate[2];
    }

    public Player() {
        super();
    }

    public Player(float width) {
        super(width);
    }

    public void switchFrame() {

    }
}
