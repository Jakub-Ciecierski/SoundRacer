package com.example.mini.game.shapes.complex;

import android.graphics.Color;

import com.example.mini.game.util.loaders.TexturesLoader;
import com.example.mini.game.util.mathematics.Vec3;

/**
 * Created by dybisz on 2015-02-21.
 */
public class SpaceDrive {
    private int DEFAULT_ENGINE_COLOR=  Color.rgb(25, 25, 255);
    private Vec3 DEFAULT_DIRECTION = new Vec3(0,0,-5);
    private float LEFT_ENGINE_PARTICLE_SIZE = 4;
    private float RIGHT_ENGINE_PARTICLE_SIZE = 4;
    private float MAIN_ENGINE_PARTICLE_SIZE = 10;
    Vec3 defaultPosition;

    Vec3 leftEnginePositionModifier = new Vec3(1.7f, -0.3f,0 );
    Vec3 rightEnginePositionModifier = new Vec3(-1.7f,-0.3f,0);
    Vec3 mainEnginePositionModifier = new Vec3(0,0,0 );

    Engine leftEngine;
    Engine rightEngine;
    Engine mainEngine;

    ParticleGenerator2 particleGenerator = new ParticleGenerator2();

    private float globalStartTime = System.nanoTime();

    int textureId = TexturesLoader.loadTexture("particle.png");

    public SpaceDrive(float[] defaultPosition) {
        leftEngine = new Engine(defaultPosition, leftEnginePositionModifier, DEFAULT_DIRECTION,
                LEFT_ENGINE_PARTICLE_SIZE, DEFAULT_ENGINE_COLOR);
        rightEngine = new Engine(defaultPosition, rightEnginePositionModifier, DEFAULT_DIRECTION,
                RIGHT_ENGINE_PARTICLE_SIZE, DEFAULT_ENGINE_COLOR);
        mainEngine = new Engine(defaultPosition, mainEnginePositionModifier, DEFAULT_DIRECTION,
                MAIN_ENGINE_PARTICLE_SIZE, DEFAULT_ENGINE_COLOR);
    }

    public void draw(float[] mvpMatrix) {
        float elapsedTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        mainEngine.createParticles(particleGenerator, elapsedTime, 25);
        leftEngine.createParticles(particleGenerator, elapsedTime, 25);
        rightEngine.createParticles(particleGenerator, elapsedTime, 25);

        particleGenerator.useProgram();
        particleGenerator.setUniforms(mvpMatrix, elapsedTime, textureId);
        particleGenerator.bindData();
        particleGenerator.draw();
    }
}
