package com.example.mini.game.shapes.basic;

import com.example.mini.game.util.mathematics.Vec3;

/**
 * Class holds all information needed to simulate a particle's
 * life.
 * <p></p>
 * Created by dybisz on 2015-02-15.
 */
public class Particle {
    /**
     * Isn't it time to die? At the beginning life is less
     * than zero because using default constructor we create particle
     * which is dead.
     */
    float life;
    /**
     * Starting position - later on changed on GPU
     * according to {@link #directionVector}.
     */
    Vec3 position;
    /**
     * Direction of particle's movement.
     */
    Vec3 directionVector;
    /**
     * During time lapse color of each particles changes.
     */
    Vec3 startColor;


    /**
     * Basic constructor.
     *
     * @param life            Fresh life time;
     * @param position        New position on the screen.
     * @param directionVector Way of movement.
     * @param startColor      Starting color of the particle.
     */
    public Particle(float life, Vec3 position, Vec3 directionVector, Vec3 startColor) {
        this.life = life;
        this.position = position;
        this.directionVector = directionVector;
        this.startColor = startColor;
    }

    public Particle() {
        this.life = -1.0f;
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getDirectionVector() {
        return directionVector;
    }

    public void setDirectionVector(Vec3 directionVector) {
        this.directionVector = directionVector;
    }

    public Vec3 getStartColor() {
        return startColor;
    }

    public void setStartColor(Vec3 startColor) {
        this.startColor = startColor;
    }
}
