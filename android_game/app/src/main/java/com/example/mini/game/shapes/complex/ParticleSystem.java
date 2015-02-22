package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;

import com.example.mini.game.shapes.basic.Particle;
import com.example.mini.game.util.shaderPrograms.ParticleShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dybisz on 2015-02-12.
 */
public abstract class ParticleSystem {
    /**
     * Maximum number of particlesContainer on screen.
     */
    protected final int MAXIMUM_NUMBER_OF_PARTICLES = 300;
    protected final int DEFAULT_NUMBER_OF_NEW_PARTICLES = 70;
    /**
     * Life of a particle until it is reset.
     */
    protected static final float DEFAULT_PARTICLE_LIFE = 100.0f;

    /**
     * List to keep managing of each particle transparent.
     */
    private List<Particle> particlesContainer = new ArrayList<Particle>(MAXIMUM_NUMBER_OF_PARTICLES);

    {
        for (int i = 0; i < MAXIMUM_NUMBER_OF_PARTICLES; i++) {
            particlesContainer.add(new Particle());
        }

    }

    /**
     * Wraps all information about shaders.
     */
    private ParticleShaderProgram particleShaderProgram = new ParticleShaderProgram();
    /**
     * VBO for particle data.
     */
//    private FloatBuffer dataVbo;
    private float[] aliveParticles =
            new float[MAXIMUM_NUMBER_OF_PARTICLES * particleShaderProgram.NUMBER_OF_ALL_COMPONENTS];

    /**
     * To speed up searching for unused particlesContainer, we keep tracking the last one
     * marked as free.
     */
    private int lastUsedParticle = 0;
    /**
     * Whole time we keep float[] array of size {@link #MAXIMUM_NUMBER_OF_PARTICLES} *
     * (bytes per float) but when we fill out only part which "is alive", we tell VBO
     * to read only as much as much information as particleCount has.
     */
    private int particleCount = 0;


    protected void render(float[] mvpMatrix) {
        float delta = (System.nanoTime() - GameBoard.GLOBAL_TIME) / 1000000000.0f;
        updateAliveParticles(1f);
        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(mvpMatrix);
        particleShaderProgram.setAttributes();
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, particleCount);
    }

    private void updateAliveParticles(float delta) {
        /* Reset value of particleCount */
        particleCount = 0;
        for (Particle p : particlesContainer) {
            p.setLife(p.getLife() - delta);
            if (p.getLife() > 0.0f) {
                /* Load position */
                aliveParticles[4 * particleCount + 0] = p.getPosition().x;
                aliveParticles[4 * particleCount + 1] = p.getPosition().y;
                aliveParticles[4 * particleCount + 2] = p.getPosition().z;
                /* Load direction vector */
                aliveParticles[4 * particleCount + 3] = p.getDirectionVector().x;
                aliveParticles[4 * particleCount + 4] = p.getDirectionVector().y;
                aliveParticles[4 * particleCount + 5] = p.getDirectionVector().z;
                /* Load color */
                aliveParticles[4 * particleCount + 6] = p.getStartColor().x;
                aliveParticles[4 * particleCount + 7] = p.getStartColor().y;
                aliveParticles[4 * particleCount + 8] = p.getStartColor().z;
                /* Load life time */
                aliveParticles[4 * particleCount + 9] = p.getLife();

            }
            particleCount++;
        }
        /* We copy to native buffer only those particles which are alive */
        particleShaderProgram.loadDataBuffer(Arrays.copyOf(aliveParticles, particleCount
                * ParticleShaderProgram.NUMBER_OF_ALL_COMPONENTS));

    }


    /**
     * Finds an index of first unused particle. Or, in case where all are used,
     * returns the first one.
     *
     * @return Index of a particle.
     */
    protected int findIndexOfUnusedParticle() {
        for (int i = lastUsedParticle; i < MAXIMUM_NUMBER_OF_PARTICLES; i++) {
            if (particlesContainer.get(i).getLife() < 0) {
                lastUsedParticle = i;
                return i;
            }
        }

        for (int i = 0; i < lastUsedParticle; i++) {
            if (particlesContainer.get(i).getLife() < 0) {
                lastUsedParticle = i;
                return i;
            }
        }

        return 0; // All particles are taken, override the first one
    }

    protected void setParticle(int index, Particle particle) {
        particlesContainer.set(index, particle);
    }

    /**
     * All classes, which inherit from this one, needs to develop
     * their own method to position new particlesContainer on a screen.
     */
    protected abstract void createNewParticles();

}
