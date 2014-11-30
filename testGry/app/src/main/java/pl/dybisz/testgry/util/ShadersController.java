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
 * Created by dybisz on 2014-11-23.
 */
public abstract class ShadersController {
    /*
        Standard vertex shader code.
     */
    public static final String vertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    /*
        Standard fragment shader code.
     */
    public static final String fragmentShader =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    /*
        Texture vertex shader code.
     */
    public static final String textureVertexShader =
           "uniform mat4 u_Matrix;\n" +
                   "attribute vec4 a_Position;\n" +
                   "attribute vec2 a_TextureCoordinates;\n" +
                   "varying vec2 v_TextureCoordinates;\n" +
                   "void main()\n" +
                   "{\n" +
                   "v_TextureCoordinates = a_TextureCoordinates;\n" +
                   "gl_Position = u_Matrix * a_Position;\n" +
                   "}";
    /*
        Texture fragment shader code.
     */
    public static final String textureFragmentShader =
           "precision mediump float;\n" +
                   "uniform sampler2D u_TextureUnit;\n" +
                   "varying vec2 v_TextureCoordinates;\n" +
                   "void main()\n" +
                   "{\n" +
                   "gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);\n" +
                   "}";

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
