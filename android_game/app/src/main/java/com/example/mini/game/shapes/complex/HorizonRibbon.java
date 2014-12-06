package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.R;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.TexturesLoader;
import com.example.mini.game.util.mathematics.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;

/**
 * Class represents a skyline of the game(visible horizon).
 * It is in the shape of bended ribbon.
 * Class also provides methods for translating and rotating the shape.
 * (used e.g. for simulation of road turning).
 * <p></p>
 * Created by dybisz on 2014-12-03.
 */
public class HorizonRibbon {
    /**
     * Vector which tells {@link #draw(float[])} how to rotate the object.
     */
    private float[] rotationVector = GameBoard.HORIZON_RIBBON_DEFAULT_ROTATION;
    /**
     * Vector which tells {@link #draw(float[])} where to translate the object.
     */
    private Vector3 translationVector = GameBoard.HORIZON_RIBBON_DEFAULT_TRANSLATION;
    /**
     * Handle to OpenGL ES shader program.
     */
    private int program;
    /**
     * Handle to the horizon texture.
     */
    private int texture;
    /**
     * Winding and coordinates of the texture.
     */
    private float[] textureCoordinates;
    /**
     * Coordinates of vertices for the object.
     */
    private float[] verticesCoordinates;
    /**
     * Buffer between Dalvik's heap and the native one for vertices coordinates.
     */
    private FloatBuffer verticesVbo;
    /**
     * Buffer between Dalvik's heap and the native one for texture coordinates.
     */
    private FloatBuffer textureVbo;
    private float rotationSpeedCounter = 0.1f;
    private float currentAngle = 0.0f;


    public HorizonRibbon() {
        this.program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureFragmentShader));
        this.texture = TexturesLoader.loadTexture(GameRenderer.context, R.drawable.space);
        this.textureCoordinates = TexturesLoader.generateUvForTriangleStrip(GameBoard.HORIZON_RIBBON_VERTICES_PER_BORDER);
        this.verticesCoordinates = generateVertices();
        loadBuffers();

    }

    private void loadBuffers() {
        verticesVbo = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        verticesVbo.position(0);

        textureVbo = ByteBuffer.allocateDirect(textureCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoordinates);
        textureVbo.position(0);
    }

    private float[] generateVertices() {
        float[] returnVertices = new float[GameBoard.HORIZON_RIBBON_VERTICES_PER_BORDER * 2 * 3];
        float offSet = 0.0f;
        float oneStep = (GameBoard.HORIZON_RIBBON_ANGLE / (float) GameBoard.HORIZON_RIBBON_VERTICES_PER_BORDER) *
                GameBoard.DEGREES_TO_RADIAN_COEFFICIENT;
        for (int i = 0; i < returnVertices.length; ) {
            /* Bottom border x, y, z. */
            returnVertices[i++] = GameBoard.HORIZON_RIBBON_RADIUS * cos(offSet);
            returnVertices[i++] = 0.0f;
            returnVertices[i++] = GameBoard.HORIZON_RIBBON_RADIUS * sin(offSet);

            /* Upper border x, y, z. */
            returnVertices[i++] = GameBoard.HORIZON_RIBBON_RADIUS * cos(offSet);
            returnVertices[i++] = GameBoard.HORIZON_RIBBON_HEIGHT;
            returnVertices[i++] = GameBoard.HORIZON_RIBBON_RADIUS * sin(offSet);

            offSet += oneStep;
        }
        return returnVertices;
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
//        Matrix.translateM(scratch, 0, mvpMatrix, 0, translationVector.getX(),
//                translationVector.getY(), translationVector.getZ());
        Matrix.rotateM(scratch, 0, mvpMatrix, 0, rotationVector[0], rotationVector[1], rotationVector[2],
                rotationVector[3]);
        Matrix.translateM(scratch, 0, translationVector.getX(), translationVector.getY(), translationVector.getZ());
        GLES20.glUniformMatrix4fv(u_TransformationMatrixHandle, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        //GLES20.glUniform4fv(uniformColorId, 1, color, 0);

         /* Draw */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, verticesCoordinates.length / 3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glDisableVertexAttribArray(a_TextureCoordinatesHandle);
    }

    public void switchFrame() {

        switch (Road.currentTurnStage) {
            case STRAIGHT:
                /* Reset this counter for next iteration */
                rotationSpeedCounter = 0.0f;
                /* Safe ribbon angle for next turning */
                currentAngle = rotationVector[0];
                break;
            case TURN_RIGHT_START:
                break;
            case TURN_LEFT_START:
                break;
            case TURN_RIGHT_STABLE:
                /* Slow rotation around Y axis */
                rotationVector[0] += 0.05;
                rotationVector[1] = 0.0f;
                rotationVector[3] = 0.0f;
                break;
            case TURN_LEFT_STABLE:
                 /* Slow rotation around Y axis */
                rotationVector[0] -= 0.05;
                rotationVector[1] = 0.0f;
                rotationVector[3] = 0.0f;
                break;
            case TURN_RIGHT_END:
                /* To simulate turning right animation correctly we need a function
                * which grows quicker as its arguments are larger and since
                * exp() is not available (do not know why) I took f(x) = (x^2)/3 */
                rotationVector[0] = currentAngle + (rotationSpeedCounter * rotationSpeedCounter) / 3f;
                rotationVector[1] = 0.0f;
                rotationVector[3] = 0.0f;
                rotationSpeedCounter += 0.1;
                break;
            case TURN_LEFT_END:
                /* To simulate turning right animation correctly we need a function
                * which grows quicker as its arguments are larger and since
                * exp() is not available (do not know why) I took f(x) = (x^2)/3 */
                rotationVector[0] = currentAngle - (rotationSpeedCounter * rotationSpeedCounter) / 3f;
                rotationVector[1] = 0.0f;
                rotationVector[3] = 0.0f;
                rotationSpeedCounter += 0.1;
                break;
        }
    }
}
