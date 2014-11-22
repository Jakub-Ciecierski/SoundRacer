package com.example.mini.myapplication;

/**
 * Created by Łukasz on 2014-11-22.
 */
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by user on 2014-11-22.
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private final Context context;
    private int PROGRAM_ID;
    private final String U_COLOR_NAME_IN_SHADER = "u_Color";
    private final String A_POSITION_NAME_IN_SHADER = "a_Position";
    private int uColorLocation;
    private int aPositionLocation;
    private int counter = 0;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    float[] tableVerticesWithTriangles = {
// Triangle 1
            -0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
// Triangle 2
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
// Line 1
            -0.5f, 0f,
            0.5f, 0f,
// Mallets
            0f, -0.25f,
            0f, 0.25f
    };

    private final String vertexShader =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 u_Matrix;\n" +
                    "attribute vec4 a_Position;\n" +
                    "attribute vec4 a_Color;\n" +
                    "varying vec4 v_Color;\n" +
                    "void main()\n" +
                    "{\n" +
                    "v_Color = a_Color;\n" +
                    "gl_Position = u_Matrix * a_Position;\n" +
                    "gl_PointSize = 10.0;\n" +
                    "}";
    private final String fragmentShader = "precision mediump float;\n" +
            "uniform vec4 u_Color;\n" +
            "void main()\n" +
            "{\n" +
            "gl_FragColor = u_Color;\n" +
            "}";
    private float[] mRotationMatrix = new float[16];


    public GameRenderer(Context context) {
        this.context = context;
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        /////////////////////////////////////////////////////////////

        // Creating objects Id
        final int vertexShaderObjectId = glCreateShader(GL_VERTEX_SHADER);
        if (vertexShaderObjectId == 0) System.out.println("Could not create new vertex shader");
        final int fragmentShaderObjectId = glCreateShader(GL_FRAGMENT_SHADER);
        if (fragmentShaderObjectId == 0) System.out.println("Could not create new fragment shader");
        // Adding source to bjects
        glShaderSource(vertexShaderObjectId, vertexShader);
        glShaderSource(fragmentShaderObjectId, fragmentShader);
        // Compiling shaders
        glCompileShader(vertexShaderObjectId);
        glCompileShader(fragmentShaderObjectId);
        // Checking status of compilation
        final int[] vertexShaderStatus = new int[1];
        glGetShaderiv(vertexShaderObjectId, GL_COMPILE_STATUS, vertexShaderStatus, 0);
        if (vertexShaderStatus[0] == 0) {
            glDeleteShader(vertexShaderObjectId);
            System.out.println("Vertex shader compilation failed.");
        }
        final int[] fragmentShaderStatus = new int[1];
        glGetShaderiv(fragmentShaderObjectId, GL_COMPILE_STATUS, fragmentShaderStatus, 0);
        if (fragmentShaderStatus[0] == 0) {
            glDeleteShader(fragmentShaderObjectId);
            System.out.println("fragment shader compilation failed");
        }
        // Linking shaders together
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) System.out.println("Could not create new program.");
        glAttachShader(programObjectId, vertexShaderObjectId);
        glAttachShader(programObjectId, fragmentShaderObjectId);
        // Linking the program
        glLinkProgram(programObjectId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            System.out.println("Linking of program failed.");
        } else
            PROGRAM_ID = programObjectId;
        // CHUJ W VALIDACJĘ
        glUseProgram(PROGRAM_ID);

        ////////////////////////////////////////////////

        uColorLocation = glGetUniformLocation(PROGRAM_ID, U_COLOR_NAME_IN_SHADER);
        aPositionLocation = glGetAttribLocation(PROGRAM_ID, A_POSITION_NAME_IN_SHADER);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation,
        /*how many components are associated with each vertex for this attribute */ 2,
                GL_FLOAT, false, /*STRIDE*/ 0, vertexData);
        /*Now that we’ve linked our data to the attribute, we need to enable the attribute
        with a call to glEnableVertexAttribArray() before we can start drawing.*/
        glEnableVertexAttribArray(aPositionLocation);

        /////////////////////////////////////////////////

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] scratch = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, 1.0f);

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        int mMVPMatrixHandle = glGetUniformLocation(PROGRAM_ID, "u_Matrix");
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);

        glDrawArrays(GL_POINTS, 8, 1);
        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
