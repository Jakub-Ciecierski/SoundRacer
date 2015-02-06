package com.example.mini.game.audio.analysis;

/**
 * Created by kuba on 2/5/15.
 */
public class Bump {
    private float value;
    private boolean isValidObstacle;

    public Bump(float value, boolean isValidObstacle) {
        this.value = value;
        this.isValidObstacle = isValidObstacle;
    }

    public Bump(float value) {
        this.value = value;
        this.isValidObstacle = false;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isValidObstacle(){
        return isValidObstacle;
    }

    public void setValidObstacle(boolean isValidObstacle) {
        this.isValidObstacle = isValidObstacle;
    }
}