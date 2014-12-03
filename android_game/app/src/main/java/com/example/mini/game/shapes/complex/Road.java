package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.R;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.TexturesLoader;
import com.example.mini.game.util.animation.VBORoadAnimation;
import com.example.mini.game.util.mathematics.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Animation "na dwa baty".
 * Created by user on 2014-11-24.
 */
public class Road {
    private float[] color;
    private float[] vertices;
    private FloatBuffer verticesVbo;
    private FloatBuffer textureVbo;
    private VBORoadAnimation vboRoadAnimation;
    private Vector3 translation = new Vector3(0f, 0f, 0f);
    private int program;
    private int texture;
    private float[] textureWinding; /*= new float[]{
        0.0f, 1.0f,     // bottom left
                0.0f, 0.0f,     // top left
                1.0f, 1.0f,     // bottom right
                1.0f, 0.0f      // top right
    };*/

    public Road(int verticesPerBorder, float timeUnitLength, float roadWidth, float[] color) {
        this.color = color;
        this.vboRoadAnimation =
                new VBORoadAnimation(verticesPerBorder, timeUnitLength, roadWidth);
        this.program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureFragmentShader));
        this.texture = TexturesLoader.loadTexture(GameRenderer.context, R.drawable.road);
        this.textureWinding = TexturesLoader.generateUvForTriangleStrip(verticesPerBorder);
        this.vertices = vboRoadAnimation.generateStartShape();
        loadBuffers();
    }

    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(program);

        int a_VertexPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        int a_TextureCoordinatesHandle = GLES20.glGetAttribLocation(program, "a_TextureCoordinates");
        int u_TransformationMatrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        int u_TextureSamplerHandle = GLES20.glGetUniformLocation(program, "u_TextureUnit");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glEnableVertexAttribArray(a_TextureCoordinatesHandle);

        GLES20.glVertexAttribPointer(a_VertexPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                /*stride*/  0, verticesVbo);
        GLES20.glVertexAttribPointer(a_TextureCoordinatesHandle, 2,
                GLES20.GL_FLOAT, false,/*stride*/  0, textureVbo);

         /* Active texture unit = 0 */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        /* Bind our loaded texture to this unit */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        /* Set unit sampler in shader to use this unit */
        GLES20.glUniform1i(u_TextureSamplerHandle, 0);


        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translation.getX(), translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(u_TransformationMatrixHandle, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        //GLES20.glUniform4fv(uniformColorId, 1, color, 0);

         /* Draw */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glDisableVertexAttribArray(a_TextureCoordinatesHandle);

    }

    public void switchFrame() {
        /* Main road update */
        vertices = vboRoadAnimation.generateNextFrame(vertices);
        textureWinding = vboRoadAnimation.generateNextTexture(textureWinding);
        loadBuffers();
        /* Adapt translation vector to the next frame of the animation */
        vboRoadAnimation.generateNextFrame(translation);
    }

    /**
     *
     */
    private void loadBuffers() {
        verticesVbo = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
        verticesVbo.position(0);

        textureVbo = ByteBuffer.allocateDirect(textureWinding.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureWinding);
        textureVbo.position(0);
    }

}
