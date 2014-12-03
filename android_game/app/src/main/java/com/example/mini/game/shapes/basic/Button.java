package com.example.mini.game.shapes.basic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.util.MoveType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;


import static java.lang.Math.abs;

/**
 * Created by user on 2014-11-26.
 */
public class Button {

    private FloatBuffer verticesBuffer;
    private ShortBuffer drawOrderBuffer;
    private int program;
    private float[] vertices = new float[12];

    private int COORDINATES_PER_VERTEX = 3;
    private float[] mScale = new float[]{1.0f, 1.0f, 1.0f};
    private float[] mTranslate = new float[3];

    private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
    private MoveType buttonType;
    private float texture[];
    private int textureId;
    private int textureId2;

    public Button(float[] upperLeft, float[] bottomRight, MoveType buttonType, int textureId, int program) {
        this.program = program;
        this.buttonType = buttonType;
        this.textureId = textureId;
        loadVertices(upperLeft, bottomRight);
        chooseTextureOrientation();
        loadBuffers();
    }

    public void updateTransformations(float[] translation, float[] scaling) {
        System.arraycopy(translation, 0, mTranslate, 0, mTranslate.length);
        System.arraycopy(scaling, 0, mScale, 0, mScale.length);
    }

    public void draw(float[] mvpMatrix, int texture) {
         /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(program);

        /* Enable transparent pixels */
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        /* Get all handles */
        int a_VertexPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        int a_TextureCoordinatesHandle = GLES20.glGetAttribLocation(program, "a_TextureCoordinates");
        int u_TransformationMatrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        int u_TextureSamplerHandle = GLES20.glGetUniformLocation(program, "u_TextureUnit");

        /* Assign attributes */
        GLES20.glVertexAttribPointer(a_TextureCoordinatesHandle, 2,
                GLES20.GL_FLOAT, false,/*stride*/  0, textureBuffer);
        GLES20.glVertexAttribPointer(a_VertexPositionHandle, COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,/*stride*/  0, verticesBuffer);

        /* Enable attributes */
        GLES20.glEnableVertexAttribArray(a_TextureCoordinatesHandle);
        GLES20.glEnableVertexAttribArray(a_VertexPositionHandle);

        /* Active texture unit = 0 */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        /* Bind our loaded texture to this unit */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        /* Set unit sampler in shader to use this unit */
        GLES20.glUniform1i(u_TextureSamplerHandle, 0);


         /* Apply additional transformations */
        float[] scratch = new float[16];
        scratch = Arrays.copyOf(mvpMatrix, mvpMatrix.length);
        Matrix.translateM(scratch, 0, mTranslate[0], mTranslate[1], mTranslate[2]);
        Matrix.scaleM(scratch, 0, mScale[0], mScale[1], mScale[2]);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(u_TransformationMatrixHandle, 1, false, scratch, 0);


        /* Draw */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glDisableVertexAttribArray(a_TextureCoordinatesHandle);

    }

    private void chooseTextureOrientation() {
        switch (buttonType) {
            case MOVE_LEFT:
                texture = new float[]{
                        0.0f, 1.0f,     // bottom left
                        0.0f, 0.0f,     // top left
                        1.0f, 1.0f,     // bottom right
                        1.0f, 0.0f      // top right
                };
                break;
            case MOVE_RIGHT:
                texture = new float[] {
                  1f,1f,
                        1f,0f,
                        0f,1f,
                        0f,0f
                };
                break;
            case MOVE_DOWN:
                texture = new float[] {
                        0f,0f,
                        1f,0f,
                        0f,1f,
                        1f,1f
                };
                break;
            case MOVE_UP:
                texture = new float[]{
                        1f, 1f,
                        0f, 1f,
                        1f, 0,
                        0f, 0f
                };
                break;

        }
    }

    private void loadBuffers() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);


        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }


    private void loadVertices(float[] upperLeft, float[] bottomRight) {
        /* Rectangle dimensions */
        float height = abs(upperLeft[1] - bottomRight[1]);
        float width = abs(upperLeft[0] - bottomRight[0]);



        /* First vertex */
        vertices[0] = upperLeft[0];
        vertices[1] = upperLeft[1] - height;
        vertices[2] = 0.0f;

         /* Second vertex */
        System.arraycopy(upperLeft, 0, vertices, 3, upperLeft.length);


        /* Third vertex */
        System.arraycopy(bottomRight, 0, vertices, 6, bottomRight.length);

        /* Forth vertex */
        vertices[9] = upperLeft[0] + width;
        vertices[10] = bottomRight[1] + height;
        vertices[11] = 0.0f;
    }


    //TODO metody do poprawki
    public void switchTexture(int textureId) {
        this.textureId = textureId;
    }

    public int getTextureId(){
        return textureId;
    }

    public void addSecondTexture(int tex) {
        this.textureId2 = tex;

    }


}
