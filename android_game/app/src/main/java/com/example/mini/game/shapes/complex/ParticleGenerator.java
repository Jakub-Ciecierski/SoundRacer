package com.example.mini.game.shapes.complex;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

import com.example.mini.game.util.loaders.ShadersLoader;
import com.example.mini.game.util.loaders.TexturesLoader;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;


/**
 * Created by dybisz on 2015-02-21.
 */
public class ParticleGenerator {

    /**
     * ********** SHADER INFO ******************
     */
    private final int program;
    private final int uMatrixLocation;
    private final int uTimeLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;
    /*********************************************/

    /**
     * *********** VERTEX DATA ************
     */
    private final float[] particles;
    private final int maxParticleCount = 300;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
                    + COLOR_COMPONENT_COUNT
                    + VECTOR_COMPONENT_COUNT
                    + PARTICLE_START_TIME_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * 4;

    private final FloatBuffer floatBuffer;
    /****************************************/

    /**
     * ******* ANIMATION INFO **************
     */
    private final int NEW_PARTICLES_PER_CYCLE = 15;
    private int currentParticleCount;
    private int nextParticle;
    private float[] position;
    private final float[] direction = new float[]{0f, 0f, -5f};
    private final int color = Color.rgb(255, 50, 5);

    private final float angleVariance = 65f;
    private final float speedVariance = 1f;

    private final Random random = new Random();

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    private float priv_globalStartTime = System.nanoTime();
    private int textureId;

    /**
     * *************************************
     */



    public ParticleGenerator(Context context, float[] translate) {
        // SHADER PROGRAM
        program = ShadersLoader.createProgram(
                ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER,
                        ShadersLoader.readShaderFromResource("particle_vertex_shader.glsl")),
                ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                        ShadersLoader.readShaderFromResource("particle_fragment_shader.glsl")));
        // UNIFORMS LOCATION
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        uTimeLocation = glGetUniformLocation(program, "u_Time");
        uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit");
        // ATTRIBUTES LOCATION
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        aColorLocation = glGetAttribLocation(program, "a_Color");
        aDirectionVectorLocation = glGetAttribLocation(program, "a_DirectionVector");
        aParticleStartTimeLocation =
                glGetAttribLocation(program, "a_ParticleStartTime");
        // VERTEX DATA
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        floatBuffer = ByteBuffer
                .allocateDirect(particles.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(particles);
        directionVector[0] = direction[0];
        directionVector[1] = direction[1];
        directionVector[2] = direction[2];
        position = translate;
        // TEXTURE
        textureId = TexturesLoader.loadTexture("particle.png");
    }


    public void draw(float[] mvpMatrix) {
        float elapsedTime = (System.nanoTime() - priv_globalStartTime) / 1000000000f;

        for (int i = 0; i < NEW_PARTICLES_PER_CYCLE; i++) {
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

            addParticle(position, color, thisDirection, elapsedTime);
        }
        useProgram();
        setUniforms(mvpMatrix, elapsedTime, textureId);
        bindData();
        draw();
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

    public void bindData() {
        int dataOffset = 0;
        setVertexAttribPointer(dataOffset,
                aPositionLocation,
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        setVertexAttribPointer(dataOffset,
                aColorLocation,
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        setVertexAttribPointer(dataOffset,
                aDirectionVectorLocation,
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        setVertexAttribPointer(dataOffset,
                aParticleStartTimeLocation,
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount,
                GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }

    public void addParticle(float[] position, int color, float[] direction,
                            float particleStartTime) {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }

        if (nextParticle == maxParticleCount) {
            // Start over at the beginning, but keep currentParticleCount so
            // that all the other particles still get drawn.
            nextParticle = 0;
        }

        particles[currentOffset++] = position[0]- 0.02f;
        particles[currentOffset++] = position[1] + 1.35f;
        particles[currentOffset++] = position[2] - 0.8f;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction[0];
        particles[currentOffset++] = direction[1];
        particles[currentOffset++] = direction[2];

        particles[currentOffset++] = particleStartTime;

        updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData, start, count);
        floatBuffer.position(0);
    }

    public void setPositionY(float y) {
        position[1] = y;
    }

}
