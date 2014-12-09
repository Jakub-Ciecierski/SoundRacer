package com.example.mini.game.util;

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
    /**
     * TODO
     */
    public static final String fogVertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "varying float v_eyeDist;" +
                    "attribute vec4 vPosition;" +
                    "uniform vec4 u_eyePos;" +
                    "void main() {" +
                    "vec4 vViewPos = uMVPMatrix*vPosition;" +
                    "v_eyeDist = sqrt( (vViewPos.x - u_eyePos.x) *\n" +
                    "                      (vViewPos.x - u_eyePos.x) +\n" +
                    "                      (vViewPos.y - u_eyePos.y) *\n" +
                    "                      (vViewPos.y - u_eyePos.y) +\n" +
                    "                      (vViewPos.z - u_eyePos.z) *\n" +
                    "                      (vViewPos.z - u_eyePos.z) );" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    /**
     * TODO
     */
    public static final String fogFragmentShader =
            "uniform vec4 u_fogColor;\n" +
                    "uniform float u_fogMaxDist;\n" +
                    "varying float v_eyeDist;\n" +
                    "uniform float u_fogMinDist;\n" +
                    "uniform vec4 u_Color;\n" +
                    "\n" +
                    "float computeLinearFogFactor()\n" +
                    "{\n" +
                    "   float factor;\n" +
                    "  \n" +
                    "    \n" +
                    "   // Compute linear fog equation\n" +
                    "   factor = (u_fogMaxDist - v_eyeDist) /\n" +
                    "            (u_fogMaxDist - u_fogMinDist );\n" +
                    "   \n" +
                    "   // Clamp in the [0,1] range\n" +
                    "   factor = clamp( factor, 0.0, 1.0 );\n" +
                    "            \n" +
                    "   return factor;            \n" +
                    "}\n" +
                    "\n" +
                    "void main(void)\n" +
                    "{\tfloat fogFactor = computeLinearFogFactor();\n" +
                    "    vec4  fogColor = fogFactor * u_fogColor;\n" +
                    "   \n" +
                    "    \n" +
                    "    // Compute final color as a lerp with fog factor\n" +
                    "    gl_FragColor = u_Color * fogFactor +\n" +
                    "                   fogColor * (1.0 - fogFactor); \n" +
                    "}";
    /**
     * TODO
     */
    public static final String textureFogVertexShader =
            "uniform mat4 uMVPMatrix;" +
                    "varying float v_eyeDist;" +
                    "attribute vec4 vPosition;" +
                    "uniform vec4 u_eyePos;" +
                    "attribute vec2 a_TextureCoordinates;\n" +
                    "varying vec2 v_TextureCoordinates;\n" +
                    "void main() {" +
                    "v_TextureCoordinates = a_TextureCoordinates;\n" +
                    "vec4 vViewPos = uMVPMatrix*vPosition;" +
                    "v_eyeDist = sqrt( (vViewPos.x - u_eyePos.x) *\n" +
                    "                      (vViewPos.x - u_eyePos.x) +\n" +
                    "                      (vViewPos.y - u_eyePos.y) *\n" +
                    "                      (vViewPos.y - u_eyePos.y) +\n" +
                    "                      (vViewPos.z - u_eyePos.z) *\n" +
                    "                      (vViewPos.z - u_eyePos.z) );" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    /**
     * TODO
     */
    public static final String textureFogFragmentShader =
            "uniform vec4 u_fogColor;\n" +
                    "uniform float u_fogMaxDist;\n" +
                    "varying float v_eyeDist;\n" +
                    "uniform float u_fogMinDist;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "varying vec2 v_TextureCoordinates;\n" +

                    "\n" +
                    "float computeLinearFogFactor()\n" +
                    "{\n" +
                    "   float factor;\n" +
                    "  \n" +
                    "    \n" +
                    "   // Compute linear fog equation\n" +
                    "   factor = (u_fogMaxDist - v_eyeDist) /\n" +
                    "            (u_fogMaxDist - u_fogMinDist );\n" +
                    "   \n" +
                    "   // Clamp in the [0,1] range\n" +
                    "   factor = clamp( factor, 0.0, 1.0 );\n" +
                    "            \n" +
                    "   return factor;            \n" +
                    "}\n" +
                    "\n" +
                    "void main(void)\n" +
                    "{\tfloat fogFactor = computeLinearFogFactor();\n" +
                    "    vec4  fogColor = fogFactor * u_fogColor;\n" +
                    "   \n" +
                    "    \n" +
                    "    // Compute final color as a lerp with fog factor\n" +
                    "vec4 baseColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "    gl_FragColor = baseColor * fogFactor +\n" +
                    "                   fogColor * (1.0 - fogFactor); \n" +
                    "}";

    /**
     * TODO
     */
    public static final String textureLightsFogVertexShader =
            "uniform mat4 u_MVPMatrix;\n" +
                    "uniform vec4 u_eyePos;\n" +
                    "\n" +
                    "attribute vec3 a_Normal;\n" +
                    "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TextureCoordinates;\n" +
                    "\n" +
                    "varying float v_eyeDist;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying vec2 v_TextureCoordinates;\n" +
                    "varying vec3 v_Position;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "  v_Position = vec3(u_MVPMatrix * a_Position);\n" +
                    "  v_Normal = vec3(u_MVPMatrix * vec4(a_Normal, 0.0));\n" +
                    "  v_TextureCoordinates = a_TextureCoordinates;\n" +
                    "  \n" +
                    "  vec4 vViewPos = u_MVPMatrix * a_Position;\n" +
                    "  v_eyeDist = sqrt(\n" +
                    "          (vViewPos.x - u_eyePos.x)*(vViewPos.x - u_eyePos.x) +\n" +
                    "          (vViewPos.y - u_eyePos.y)*(vViewPos.y - u_eyePos.y) +\n" +
                    "          (vViewPos.z - u_eyePos.z)*(vViewPos.z - u_eyePos.z) \n" +
                    "    );\n" +
                    "    \n" +
                    "  gl_Position = u_MVPMatrix*a_Position;\n" +
                    "}";
    /**
     * TODO
     */
    public static final String textureLightsFogFragmentShader =
            "precision mediump float;\n" +
                    "\n" +
                    "uniform vec4 u_fogColor;\n" +
                    "uniform float u_fogMaxDist;\n" +
                    "uniform vec3 u_LightPosition;\n" +
                    "uniform float u_fogMinDist;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "\n" +
                    "varying vec2 v_TextureCoordinates;\n" +
                    "varying vec3 v_Normal;\n" +
                    "varying float v_eyeDist;\n" +
                    "varying vec3 v_Position;\n" +
                    "\n" +
                    "float computeLinearFogFactor() \n" +
                    "{\n" +
                    "  float factor;\n" +
                    "  factor = (u_fogMaxDist - v_eyeDist) / (u_fogMaxDist - u_fogMinDist);\n" +
                    "  factor = clamp(factor, 0.0,1.0);\n" +
                    "  return factor;\n" +
                    "}\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "  float fogFactor = computeLinearFogFactor();\n" +
                    "  vec4 fogColor = fogFactor * u_fogColor;\n" +
                    "  \n" +
                    "  vec3 lightVector = normalize(u_LightPosition - v_Position);\n" +
                    "  float diffuse = max(dot(v_Normal, lightVector),0.0);\n" +
                    "  gl_FragColor = diffuse * texture2D(u_TextureUnit,v_TextureCoordinates)*fogFactor + fogColor*(1.0 - fogFactor);\n" +
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
