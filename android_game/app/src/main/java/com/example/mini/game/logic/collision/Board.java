package com.example.mini.game.logic.collision;

import android.opengl.GLES20;

import com.example.mini.game.util.ShadersController;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Kuba on 05/12/2014.
 */
public class Board {
    /** The buffer holding the vertices */
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    static final int COORDS_PER_VERTEX = 3;
    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes pe

    /*
    private float vertices[] = {
            -1.0f, -1.0f, 0.0f, 	//Bottom Left
            1.0f, -1.0f, 0.0f, 		//Bottom Right
            -1.0f, 1.0f, 0.0f,	 	//Top Left
            1.0f, 1.0f, 0.0f 		//Top Right
    };
*/
    private float vertices[] =  { -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f }; // top right

    private short drawOrder[] = {0,1,2,0,2,3};

    //float color[] = {0, 1.0f, 0.5f, 1.0f};
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    private int programId;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    public Board () {
        vertexCount = vertices.length / COORDS_PER_VERTEX;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
    }

    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(programId);

        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(programId, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(programId, "vColor");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
        /*stride*/  vertexStride, vertexBuffer);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpId, 1, false, mvpMatrix, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the cube
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);


        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
