package com.example.mini.game.shapes.complex;

import com.example.mini.game.util.mathematics.Vec3;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

/**
 * Created by dybisz on 2015-02-21.
 */
public class Engine {
    private Vec3 positionModifier;
    private final Vec3 direction;
    private int color;
    private float particleSize;
    private final Random random = new Random();

    private final float angleVariance = 55f;
    private final float speedVariance = 2f;

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    private float[] defaultPosition;


    public Engine(float[] defaultPosition, Vec3 position, Vec3 direction,float particleSize, int color) {
        this.defaultPosition = defaultPosition;

        this.positionModifier = position;
        this.direction = direction;
        this.color = color;
        this.particleSize = particleSize;

        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void createParticles(ParticleGenerator2 particleGenerator2,float elapsedTime, int numberOfNewParticles ) {
        for (int i = 0; i < numberOfNewParticles; i++) {
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    1);

            multiplyMV(
                    resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0);

            float speedAdjustment = 1f + random.nextFloat() * speedVariance;

            float[] thisDirection = new float[]{
                    resultVector[0] * speedAdjustment,
                    resultVector[1] * speedAdjustment,
                    resultVector[2] * speedAdjustment};

            Vec3 finalPosition = new Vec3(defaultPosition[0] + positionModifier.x,
                                            defaultPosition[1] + positionModifier.y,
                                            defaultPosition[2] + positionModifier.z);

            particleGenerator2.addParticle(finalPosition, color, particleSize, thisDirection, elapsedTime);
        }
    }

}
