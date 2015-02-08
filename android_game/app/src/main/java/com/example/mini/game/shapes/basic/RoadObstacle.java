package com.example.mini.game.shapes.basic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * As we have changed the way in which obstacle are treated
 * (now they are "a part of the road"), new class with desired
 * information is needed. Here it is. It is fully suitable for
 * filling obstacles array in {@link com.example.mini.game.shapes.complex.Road}
 * class.
 * <p></p>
 * Created by dybisz on 2015-02-07.
 */
public class RoadObstacle {
    public final static int COORDINATES_PER_VERTEX = 3;
    public final static int NUMBER_OF_VERTICES = 8;
    private final static float DEFAULT_HEIGHT = 4.0f;
    private final static float DEFAULT_WIDTH = 2.0f;
    private final static float DEFAULT_DEPTH = 2.0f;
    private float[] coordinates;
    private float[] vertices;
    FloatBuffer vertexBuffer;

    public RoadObstacle(float x, float y, float z) {
        coordinates = new float[]{x, y, z};
        generateVertices();
        loadBuffer();
    }

    private void loadBuffer() {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
        vertexBuffer.position(0);
    }

    public FloatBuffer getVerticesVbo() {
        return vertexBuffer;
    }

    private void generateVertices() {
        vertices = new float[NUMBER_OF_VERTICES * COORDINATES_PER_VERTEX];
        /* Vertex 0 */
        vertices[0] = coordinates[0];
        vertices[1] = coordinates[1];
        vertices[2] = coordinates[2] + DEFAULT_DEPTH;
        /* Vertex 1 */
        vertices[3] = coordinates[0];
        vertices[4] = coordinates[1];
        vertices[5] = coordinates[2];
        /* Vertex 2 */
        vertices[6] = coordinates[0];
        vertices[7] = coordinates[1] + DEFAULT_HEIGHT;
        vertices[8] = coordinates[2] + DEFAULT_DEPTH;
        /* Vertex 3 */
        vertices[9] = coordinates[0];
        vertices[10] = coordinates[1] + DEFAULT_HEIGHT;
        vertices[11] = coordinates[2];
        /* Vertex 4 */
        vertices[12] = coordinates[0] + DEFAULT_WIDTH;
        vertices[13] = coordinates[1] + DEFAULT_HEIGHT;
        vertices[14] = coordinates[2];
        /* Vertex 5 */
        vertices[15] = coordinates[0] + DEFAULT_WIDTH;
        vertices[16] = coordinates[1];
        vertices[17] = coordinates[2];
        /* Vertex 6 */
        vertices[18] = coordinates[0] + DEFAULT_WIDTH;
        vertices[19] = coordinates[1];
        vertices[20] = coordinates[2] + DEFAULT_DEPTH;
        /* Vertex 7 */
        vertices[21] = coordinates[0] + DEFAULT_WIDTH;
        vertices[22] = coordinates[1] + DEFAULT_HEIGHT;
        vertices[23] = coordinates[2] + DEFAULT_DEPTH;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public float[] getVertices() {
        return vertices;
    }
}
