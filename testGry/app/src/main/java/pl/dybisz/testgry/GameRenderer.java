package pl.dybisz.testgry;

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

import pl.dybisz.testgry.shapes.Triangle;
import pl.dybisz.testgry.shapes.Cube;
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
//    private final FloatBuffer vertexData;
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
    private Cube cube;

//    float[] tableVerticesWithTriangles = {
//// Triangle 1
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//// Triangle 2
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            0.5f, 0.5f,
//// Line 1
//            -0.5f, 0f,
//            0.5f, 0f,
//// Mallets
//            0f, -0.25f,
//            0f, 0.25f
//    };


    private float[] mRotationMatrix = new float[16];


    public GameRenderer(Context context) {
        this.context = context;
//        vertexData = ByteBuffer
//                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        cube = new Cube();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1,1, 200);
        //Matrix.orthoM(mProjectionMatrix,0, -ratio, ratio, -1,1,-1,1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//        float[] scratch = new float[16];
//        Matrix.setLookAtM(mViewMatrix, 0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.0009f * ((int) time);
//        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, 1.0f);
//
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//
//        int mMVPMatrixHandle = glGetUniformLocation(PROGRAM_ID, "u_Matrix");
//        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);
//
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,angle,angle,-5 , angle, angle, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        glClear(GL_COLOR_BUFFER_BIT);
        cube.draw(mMVPMatrix);
    }
}
