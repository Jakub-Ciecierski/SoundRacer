package pl.dybisz.testgry.util;

import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;

/**
 * Created by user on 2014-11-23.
 */
public abstract class ShadersController {
    public static int loadShader(int type, String shaderCode) {
        /* Create and verify */
        int shaderId = GLES20.glCreateShader(type);
        if (shaderId == 0) Log.i("ERROR", "COULD NOT CREATE SHADER");

        /* Set source code */
        GLES20.glShaderSource(shaderId, shaderCode);

        /* Compile and verify */
        GLES20.glCompileShader(shaderId);
        final int[] shaderCompileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, shaderCompileStatus, 0);
        if (shaderCompileStatus[0] == 0) {
            glDeleteShader(shaderId);
            //System.out.println(type + " shader compilation failed.");
            Log.i("ERROR", "COMPILING " + type + "SHADER FAILED");
        }

        return shaderId;
    }

    public static int createProgram(int vertexShaderId, int fragmentShaderId) {
        /* Create empty program and verify */
        int programId = GLES20.glCreateProgram();
        if (programId == 0) Log.i("ERROR", "PROGRAM CREATION");

        /* Link shaders with program */
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        /* Link and verify program itself */
        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            //System.out.println("Linking of program failed.");
            Log.i("ERROR", "LINKING PROGRAM");
        }

        return programId;
    }
}
