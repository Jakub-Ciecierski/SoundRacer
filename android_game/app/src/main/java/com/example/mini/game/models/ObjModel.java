package com.example.mini.game.models;

import android.util.Log;

import com.example.mini.game.CustomGlSurfaceView;
import com.example.mini.game.GameRenderer;
import com.example.mini.game.R;
import com.example.mini.game.util.TexturesLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class has huge constraints i.e.:<p/>
 * >> it loads only vertices coordinates and faces drawing order<p/>
 * >> faces coordinates must be length 3 i.e all face must be a triangle.
 * <p><p/>
 * When we consider Blender program, all faces must be 'beautify' and no textures,
 * lights, animation etc. can be applied. Just a raw model.
 * <p></p>
 * Created by dybisz on 2014-12-11.
 */
public class ObjModel {
    /**
     * Vertex prefix in .obj standard.
     */
    private static final String VERTEX = "v";
    /**
     * Face prefix in .obj standard.
     */
    private static final String FACE = "f";
    /**
     * UV prefix in .obj standard.
     */
    private static final String UV = "vt";
    /**
     * Constant indicates that we deal with  triangle face.
     */
    private static final int TRIANGLE_FACE = 4;
    /**
     * Constant indicates that we deal with quadrilateral face
     * and some extra actions need to be performed.
     */
    private static final int QUADRILATERAL_FACE = 5;

    /**
     * Array for vertices coordinates.
     */
    private List<Float> vertices = new ArrayList<Float>();
    /**
     * Array for faces drawing order.
     */
    private List<Short> faces = new ArrayList<Short>();
    /**
     * Array for UV coordinates. Just to store them - no order involved.
     */
    private List<Float> uvCoordinates = new ArrayList<Float>();
    public List<Float> tempUV;
    /**
     * Array for uvCoordinates<->vertex association. In other words mapping
     * to the {@link #faces} telling when, which uvCoordinates need to be drawn.ł
     */
    private List<Short> uvOrder = new ArrayList<Short>();
    /**
     * Texture ID in OpenGL program
     */
    private int textureId;

    /**
     * Main constructor.
     *
     * @param objId Name of an .obj file to load.
     */
    public ObjModel(int objId, int texId) {
        processObj(objId);
        this.textureId = TexturesLoader.loadTexture(GameRenderer.context, texId);
    }

    /**
     * Method parse .obj and extract information(for now only vertices and faces).
     *
     * @param objId Name of an .obj file to process.
     *              Given file must be in models/obj folder.
     */
    private void processObj(int objId) {
        BufferedReader buffer;

        /* Try to open a file and adjust buffer reader */
        try {
            InputStream objFile =
                    CustomGlSurfaceView.context.getResources().openRawResource(objId);
            buffer = new BufferedReader(
                    new InputStreamReader(objFile));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        String line;

        try {
            while ((line = buffer.readLine()) != null) {
                /* Split current line into tokes separated by whitespaces */
                StringTokenizer parts = new StringTokenizer(line, " ");

                /* If line is empty skip it */
                int tokensCount = parts.countTokens();
                if (tokensCount == 0)
                    continue;

                /* First token in the line is always a type of data */
                String type = parts.nextToken();

                /* Depending on data type we perform adequate actions */
                if (type.equals(VERTEX)) {
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                } else if (type.equals(FACE)) {
                    switch (tokensCount) {
                        case TRIANGLE_FACE:
                            /* Split next token to vertex and uv*/

                            // X
                            StringTokenizer facePart = new StringTokenizer(parts.nextToken(), "/");
                            faces.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            uvOrder.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            // Y
                            facePart = new StringTokenizer(parts.nextToken(), "/");
                            faces.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            uvOrder.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            // Z
                            facePart = new StringTokenizer(parts.nextToken(), "/");
                            faces.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            uvOrder.add((short) (Short.parseShort(facePart.nextToken()) - 1));
                            //
                            Log.i("FACES", "" + faces.get(faces.size() - 3)
                                            + " " + faces.get(faces.size() - 2)
                                            + " " + faces.get(faces.size() - 1)
                            );
                            break;
                        case QUADRILATERAL_FACE:
                            break;
                    }
                } else if (type.equals(UV)) {
                    uvCoordinates.add(Float.parseFloat(parts.nextToken()));
                    uvCoordinates.add(1.0f - Float.parseFloat(parts.nextToken()));
                    // CHECK
//                    Log.i("UV COORDS ADDED", "" + uvCoordinates.get(uvCoordinates.size() - 2)
//                                    + " " + uvCoordinates.get(uvCoordinates.size() - 1)
//                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        /* Map UV coordinates to draw order */
        List<Float> newUvOrder = new ArrayList<Float>();
//        for (int i = 0; i < uvOrder.size(); i++) {
//            int currentUvIndex = uvOrder.get(i);
//            //Log.i("current_index:","" + currentUvIndex);
//            // -------------------
//            newUvOrder.add(uvCoordinates.get(2 * currentUvIndex));
//            newUvOrder.add(uvCoordinates.get((2 * currentUvIndex) + 1));
//
//            Log.i("INDEX", "i: " + i +" index: " + currentUvIndex + " U: " + newUvOrder.get(newUvOrder.size() - 2)
//                            + " V: " + newUvOrder.get(newUvOrder.size() - 1)
//            );
//
//        }

        for(int i =0; i < vertices.size(); i++) {

        }

        tempUV = newUvOrder;
        /***********************************/
    }

    /**
     * Returns list of vertices coordinates as a float array.
     *
     * @return Float array of vertices coordinates.
     */
    public float[] getVertices() {
        float[] floatVertices = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatVertices[i] = vertices.get(i);
        }
        return floatVertices;
    }

    /**
     * Returns list of faces draw order coordinates as a float array.
     *
     * @return Float array of faces draw order.
     */
    public short[] getFaces() {
        short[] floatFaces = new short[faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            floatFaces[i] = faces.get(i);
        }
        return floatFaces;
    }

    /**
     * Returns size of {@link #vertices} array.
     *
     * @return Size of {@link #vertices}.
     */
    public int getVerticesSize() {
        return vertices.size();
    }

    public float[] getTextures() {
        float[] floatTextures = new float[tempUV.size()];
        for (int i = 0; i < tempUV.size(); i++) {
            floatTextures[i] = tempUV.get(i);
        }
        return floatTextures;
    }

    public int getTexturesSize() {
        return tempUV.size();
    }


    /**
     * Returns size of {@link #faces} array.
     *
     * @return Size of {@link #faces}.
     */
    public int getFacesSize() {
        return faces.size();
    }

    public int getTextureId() {
        return textureId;
    }
}
