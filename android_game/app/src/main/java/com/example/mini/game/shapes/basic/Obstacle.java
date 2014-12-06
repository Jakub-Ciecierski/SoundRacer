package com.example.mini.game.shapes.basic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.mathematics.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * Created by user on 2014-11-30.
 */
public class Obstacle {
    private final static int COORDINATES_PER_VERTEX = 3;
    private final static int NUMBER_OF_VERTICES = 8;
    private int program;
    private float[] vertices;
    private float[] color;
    private short[] drawOrder = new short[]{
            0, 3, 1, 0, 2, 3,
            5, 1, 3, 3, 4, 5,
            6, 5, 4, 4, 7, 6,

    };

    FloatBuffer vertexBuffer;
    ShortBuffer drawOrderBuffer;
    Vector3 translation = new Vector3(0.0f, 0.0f, 0.0f);
    int fogProgram;
    /**
     * Each obstacle is hooked to some vertex. By decreasing an index of
     * assigned vertex we move obstacle towards the player's camera.
     * Related methods:
     * {@link #getAssignedVertexIndex()} } and
     * {@link #decrementAssignedVertex()}
     * By default we assign to this field max value
     */
    private int assignedVertexIndex = GameBoard.ROAD_VERTICES_PER_BORDER;
    /**
     * When we generate new obstacles on the road we have different position available.
     * This coefficient tells how far from from inner vertex lies the obstacle.
     * 1.0f means outer vertex.
     */
    private float assignedVertexVectorCoefficient = 1.0f;

    public Obstacle(float width, float height, float depth, float[] color) {
        this.color = color;
        program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
        fogProgram = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.fogVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fogFragmentShader));
        generateVertices(width, height, depth);
        loadBuffers();
    }

    public void draw(float[] mvpMatrix) {
         /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(program);

        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(program, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(program, "vColor");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translation.getX(),
                translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    private void generateVertices(float width, float height, float depth) {
        vertices = new float[NUMBER_OF_VERTICES * COORDINATES_PER_VERTEX];
        /* Vertex 0 */
        vertices[0] = 0.0f;
        vertices[1] = 0.0f;
        vertices[2] = depth;
        /* Vertex 1 */
        vertices[3] = 0.0f;
        vertices[4] = 0.0f;
        vertices[5] = 0.0f;
        /* Vertex 2 */
        vertices[6] = 0.0f;
        vertices[7] = height;
        vertices[8] = depth;
        /* Vertex 3 */
        vertices[9] = 0.0f;
        vertices[10] = height;
        vertices[11] = 0.0f;
        /* Vertex 4 */
        vertices[12] = width;
        vertices[13] = height;
        vertices[14] = 0.0f;
        /* Vertex 5 */
        vertices[15] = width;
        vertices[16] = 0.0f;
        vertices[17] = 0.0f;
        /* Vertex 6 */
        vertices[18] = width;
        vertices[19] = 0.0f;
        vertices[20] = depth;
        /* Vertex 7 */
        vertices[21] = width;
        vertices[22] = height;
        vertices[23] = depth;
    }

    private void loadBuffers() {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
        vertexBuffer.position(0);

        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(drawOrder);
        drawOrderBuffer.position(0);
    }

    public void setTranslationCoordinates(Vector3 translations) {
        translation.setX(translations.getX());
        translation.setY(translations.getY());
        translation.setZ(translations.getZ());
    }

    public float getTranslationZ() {
        return translation.getZ();
    }

    public void setTranslationZ(float z) {
        translation.setZ(z);
    }

    public void fogDraw(float[] mvpMatrix) {
         /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(fogProgram);



        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(fogProgram, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(fogProgram, "u_Color");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(fogProgram, "uMVPMatrix");
        int u_fogColor_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogColor");
        int u_fogMaxDist_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogMaxDist");
        int u_fogMinDist_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogMinDist");
        int u_eyePos_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_eyePos");

        // ASSIGN VALUES
        GLES20.glUniform4fv(u_fogColor_HANDLE, 1, new float[]{0.7f, 0.2f, 1f, 1.0f}, 0);
        GLES20.glUniform4fv(u_eyePos_HANDLE, 1, GameRenderer.getEyePosition(), 0);
        GLES20.glUniform1f(u_fogMaxDist_HANDLE,
                GameBoard.ROAD_VERTICES_PER_BORDER * 0.8f);
        GLES20.glUniform1f(u_fogMinDist_HANDLE, 0.0f);




        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translation.getX(),
                translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    /**
     * Getter for {@link #assignedVertexIndex} field;
     *
     * @return Current value of {@link #assignedVertexIndex} field.
     */
    public int getAssignedVertexIndex() {
        return assignedVertexIndex;
    }

    /**
     * Assigns value for {@link #assignedVertexIndex} field.
     *
     * @param value New value for{@link #assignedVertexIndex}.
     */
    public void setAssignedVertexIndex(int value) {
        assignedVertexIndex = value;
    }

    /**
     * Decreases value of {@link #assignedVertexIndex assignedVertexIndex} by 1;
     * It also provides that vertex is not smaller than 0.
     */
    public void decrementAssignedVertex() {
        if (assignedVertexIndex > 0)
            assignedVertexIndex--;
    }

    /**
     * Sets {@link #translation} field.
     *
     * @param newVec new vector which will be assigned to {@link #translation} field.
     */
    public void setTranslation(Vector3 newVec) {
        translation.setX(newVec.getX());
        translation.setY(newVec.getY());
        translation.setZ(newVec.getZ());
    }

    /**
     * Retrieve value of {@link #assignedVertexVectorCoefficient}.
     *
     * @return Value of {@link #assignedVertexVectorCoefficient}.
     */
    public float getAssignedVertexVectorCoefficient() {
        return assignedVertexVectorCoefficient;
    }

    /**
     * Sets value of {@link #assignedVertexVectorCoefficient}
     *
     * @param assignedVertexVectorCoefficient new value for
     *                                        {@link #assignedVertexVectorCoefficient}.
     */
    public void setAssignedVertexVectorCoefficient(float assignedVertexVectorCoefficient) {
        this.assignedVertexVectorCoefficient = assignedVertexVectorCoefficient;
    }
}
