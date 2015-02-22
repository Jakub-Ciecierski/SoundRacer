package com.example.mini.game.util.shaderPrograms;

import android.opengl.GLES20;

import com.example.mini.game.util.loaders.ShadersLoader;
import com.example.mini.game.util.vbo.FloatVbo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wraps all information about shaders uniforms and attributes to keep
 * Other classes clean.
 * <p></p>
 * Created by dybisz on 2015-02-15.
 */
public class ParticleShaderProgram {
    private static final String U_MATRIX = "u_Matrix";
    private static final String A_PARTICLE_LIFE = "a_ParticleLife";
    private static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";

    private static final int NUMBER_OF_POSITION_COMPONENTS = 3;
    private static final int NUMBER_OF_DIRECTION_COMPONENTS = 3;
    private static final int NUMBER_OF_COLOR_COMPONENTS = 3;
    private static final int NUMBER_OF_LIFE_TIME_COMPONENTS = 1;
    public static final int NUMBER_OF_ALL_COMPONENTS =
            NUMBER_OF_POSITION_COMPONENTS +
                    NUMBER_OF_DIRECTION_COMPONENTS +
                    NUMBER_OF_COLOR_COMPONENTS +
                    NUMBER_OF_LIFE_TIME_COMPONENTS;
    FloatVbo dataBuffer;
    private static final int STRIDE = NUMBER_OF_ALL_COMPONENTS * 4;
    /**
     * Each particle will use the same set of shader so we need to load
     * the program only once.
     */
    private int program = ShadersLoader.createProgram(
            ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShadersLoader.readShaderFromResource("particle_vertex_shader.glsl")),
            ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersLoader.readShaderFromResource("particle_fragment_shader.glsl")));
    /**
     *
     */
    private int u_Matrix = GLES20.glGetUniformLocation(program, U_MATRIX);
    /**
     *
     */
    public int a_Position = GLES20.glGetAttribLocation(program, A_POSITION);
    public int a_Color = GLES20.glGetAttribLocation(program, A_COLOR);
    public int a_DirectionVector = GLES20.glGetAttribLocation(program, A_DIRECTION_VECTOR);
    public int a_ParticleLife = GLES20.glGetAttribLocation(program, A_PARTICLE_LIFE);


    public void setUniforms(float[] mvpMatrix) {
        GLES20.glUniformMatrix4fv(u_Matrix, 1, false, mvpMatrix, 0);
    }

    public void loadDataBuffer(float[] newData) {
        dataBuffer = new FloatVbo(newData);
    }
    public void setAttributes() {
        int dataOffset = 0;
        dataBuffer.setVertexAttributePointer(dataOffset,
                a_Position,
                NUMBER_OF_POSITION_COMPONENTS, STRIDE);
        dataOffset += NUMBER_OF_POSITION_COMPONENTS;

        dataBuffer.setVertexAttributePointer(dataOffset,
                a_DirectionVector,
                NUMBER_OF_DIRECTION_COMPONENTS, STRIDE);
        dataOffset += NUMBER_OF_DIRECTION_COMPONENTS;

        dataBuffer.setVertexAttributePointer(dataOffset,
                a_Color,
                NUMBER_OF_COLOR_COMPONENTS, STRIDE);
        dataOffset += NUMBER_OF_COLOR_COMPONENTS;

        dataBuffer.setVertexAttributePointer(dataOffset,
                a_ParticleLife,
                NUMBER_OF_LIFE_TIME_COMPONENTS, STRIDE);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
