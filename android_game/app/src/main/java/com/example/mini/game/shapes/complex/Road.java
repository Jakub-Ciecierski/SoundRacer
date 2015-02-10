package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.R;
import com.example.mini.game.util.enums.TurnStage;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.TexturesLoader;
import com.example.mini.game.util.animation.VBORoadAnimation;
import com.example.mini.game.util.lists.RoadObstaclesList;
import com.example.mini.game.util.lists.RoadVerticesList;
import com.example.mini.game.util.mathematics.Vector;
import com.example.mini.game.util.mathematics.Vector3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import static android.util.FloatMath.sin;

/**
 * Animation "na dwa baty".
 * Created by user on 2014-11-24.
 */
public class Road {
    public static float totalZTranslation = 0.0f;
    private final int fogLightsProgram;
    private float[] color;

    public static RoadVerticesList vertices = new RoadVerticesList();
    public static RoadObstaclesList obstacles = new RoadObstaclesList();

    private static FloatBuffer verticesVbo;
    private static FloatBuffer textureVbo;
    private static FloatBuffer normalsVbo;

    public static Vector3 translation = new Vector3(0f, 0f, 0f);
    public float[] rotation = new float[]{0.0f, 1.0f, 1.0f, 1.0f};

    private int texture;
    private static float[] textureWinding;

    public static TurnStage currentTurnStage = TurnStage.STRAIGHT;
    private static float[] normals;
    private float lightCounter = 0.0f;

    public Road(int verticesPerBorder, float timeUnitLength, float roadWidth, float[] color) {
        this.color = color;
        this.fogLightsProgram = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureLightsFogVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureLightsFogFragmentShader));
        this.texture = TexturesLoader.loadTexture(GameRenderer.context, R.drawable.road_alternative);
        this.textureWinding = TexturesLoader.generateUvForTriangleStrip(verticesPerBorder);
        this.vertices.generateStartShape();
        this.normals = generateNormals(vertices);
        loadBuffers();
    }

    /**
     * Method generates normals for a give set of vertices.
     * It also sets last two normals as (0,0,0).
     *
     * @param vertices
     * @return
     */
    private float[] generateNormals(RoadVerticesList vertices) {
        float[] normalPerVertex = new float[vertices.length()*3];
        /* All vertices but last two, since we do not see them we set
          (0,0,0) vectors as normals*/
        int d =0;
        for (int i = 0; i < vertices.length()-2; i+=2) {
            /* Vector from current inned vertex to next inner vertex */
            float[] innersVector = new float[]{
                    vertices.get(i + 2).x - vertices.get(i).x,
                    vertices.get(i + 2).y - vertices.get(i).y,
                    vertices.get(i + 2).z - vertices.get(i).z
            };
            /* Vector from current inner vertex to corresponding outer vertex */
            float[] innerOuterVector = new float[]{
                    vertices.get(i + 1).x - vertices.get(i).x,
                    vertices.get(i + 1).y - vertices.get(i).y,
                    vertices.get(i + 1).z - vertices.get(i).z
            };
            /* Calculate and normalize cross product of the two above */
            float[] crossProduct = Vector.crossProduct(innersVector, innerOuterVector);
            crossProduct = Vector.normalize(crossProduct);

            /* Assign normal to inner vertex */
            normalPerVertex[d++] = crossProduct[0];
            normalPerVertex[d++] = crossProduct[1];
            normalPerVertex[d++] = crossProduct[1];

            /* Assign normal to outer vertex */
            normalPerVertex[d++] = crossProduct[0];
            normalPerVertex[d++] = crossProduct[1];
            normalPerVertex[d++] = crossProduct[2];

        }
        return normalPerVertex;
    }



    public void switchFrame() {
//        vertices = VBORoadAnimation.generateNewShit(vertices, translation);
        textureWinding = VBORoadAnimation.generateNextTexture(textureWinding);
//        loadBuffers();
    }

    public static void nextVertexRoad() {
        vertices = VBORoadAnimation.generateNewShit(vertices);
//        textureWinding = VBORoadAnimation.generateNextTexture(textureWinding);
        loadBuffers();
//        vboRoadAnimation.translateByTimeUnit(translation);
    }


    /**
     *
     */
    private static void loadBuffers() {
        verticesVbo = ByteBuffer.allocateDirect(vertices.asFloatArray().length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices.asFloatArray());
        verticesVbo.position(0);

        textureVbo = ByteBuffer.allocateDirect(textureWinding.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureWinding);
        textureVbo.position(0);

        normalsVbo = ByteBuffer.allocateDirect(normals.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(normals);
        normalsVbo.position(0);
    }
    /*
    DRAW TEXTURES< LIGHTS AND FOOOOOOGGGGG MAAAAN!
     */
    public void lightsFogDraw(float[] mvpMatrix) {
        //obstacles.draw(mvpMatrix);
           /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(fogLightsProgram);

        int a_VertexPositionHandle = GLES20.glGetAttribLocation(fogLightsProgram, "a_Position");
        int a_TextureCoordinatesHandle = GLES20.glGetAttribLocation(fogLightsProgram, "a_TextureCoordinates");
        int a_Normal_HANDLE = GLES20.glGetAttribLocation(fogLightsProgram, "a_Normal");
        int u_TransformationMatrixHandle = GLES20.glGetUniformLocation(fogLightsProgram, "u_MVPMatrix");
        int u_TextureSamplerHandle = GLES20.glGetUniformLocation(fogLightsProgram, "u_TextureUnit");
        int u_fogColor_HANDLE = GLES20.glGetUniformLocation(fogLightsProgram, "u_fogColor");
        int u_fogMaxDist_HANDLE = GLES20.glGetUniformLocation(fogLightsProgram, "u_fogMaxDist");
        int u_fogMinDist_HANDLE = GLES20.glGetUniformLocation(fogLightsProgram, "u_fogMinDist");
        int u_eyePos_HANDLE = GLES20.glGetUniformLocation(fogLightsProgram, "u_eyePos");
        int u_lightPosition_HANDLE = GLES20.glGetUniformLocation(fogLightsProgram, "u_LightPosition");


        GLES20.glUniform4fv(u_fogColor_HANDLE, 1, new float[]{0.7f, 0.2f, 1f, 1.0f}, 0);
        GLES20.glUniform4fv(u_eyePos_HANDLE, 1, GameRenderer.getEyePosition(), 0);
        GLES20.glUniform1f(u_fogMaxDist_HANDLE,
                GameBoard.ROAD_VERTICES_PER_BORDER );
        GLES20.glUniform1f(u_fogMinDist_HANDLE, 50.0f);
        float z = 350*sin(lightCounter+=0.05f)+10.0f;
        GLES20.glUniform3fv(u_lightPosition_HANDLE, 1,new float[] {50, 120, 90}, 0);
        // Log.i("lighCounter",""+lightCounter);


        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glEnableVertexAttribArray(a_TextureCoordinatesHandle);
        GLES20.glEnableVertexAttribArray(a_Normal_HANDLE);

        GLES20.glVertexAttribPointer(a_VertexPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                /*stride*/  0, verticesVbo);
        GLES20.glVertexAttribPointer(a_TextureCoordinatesHandle, 2,
                GLES20.GL_FLOAT, false,/*stride*/  0, textureVbo);
        GLES20.glVertexAttribPointer(a_Normal_HANDLE, 3,
                GLES20.GL_FLOAT, false, 0, normalsVbo);

         /* Active texture unit = 0 */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        /* Bind our loaded texture to this unit */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        /* Set unit sampler in shader to use this unit */
        GLES20.glUniform1i(u_TextureSamplerHandle, 0);


        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];

//       Matrix.translateM(scratch, 0, mvpMatrix, 0, translation.getX(), translation.getY(), translation.getZ());
//        Matrix.rotateM(scratch,0,rotation[0],rotation[1],rotation[2],rotation[3]);
        Matrix.rotateM(scratch, 0, mvpMatrix, 0, rotation[0], rotation[1], rotation[2], rotation[3]);
        Matrix.translateM(scratch, 0, translation.getX(), translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(u_TransformationMatrixHandle, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        //GLES20.glUniform4fv(uniformColorId, 1, color, 0);

         /* Draw */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length());

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glDisableVertexAttribArray(a_TextureCoordinatesHandle);
        GLES20.glDisableVertexAttribArray(a_Normal_HANDLE);
    }

    /**
     * Returns array with coordinates of vertices numberOfVertex and numberOfVertex+1
     * (in this order). Remember that numering starts from 1 and ends with
     * {@link com.example.mini.game.shapes.complex.GameBoard#ROAD_VERTICES_PER_BORDER}.
     *
     * @param numberOfVertex Specify which vertex you want to get.ł
     * @return Array with coordinates of appropriate vertices.
     */
    public static float[] getVertices(int numberOfVertex) {
//        if (numberOfVertex > 0 && numberOfVertex <= GameBoard.ROAD_VERTICES_PER_BORDER) {
//            int k = (numberOfVertex - 1) * 6;
//            return new float[]{
//             /* Inner vertex */
//                    vertices[k], vertices[k + 1], (vertices[k + 2] - totalZTranslation),
//             /* Outer vertex */
//                    vertices[k + 3], vertices[k + 4], (vertices[k + 5] - totalZTranslation),
//            };
//        } else
//            return null;
        return null;
    }
}
