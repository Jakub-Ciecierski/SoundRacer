package com.example.mini.game.models;

import android.util.Log;

import com.example.mini.game.CustomGlSurfaceView;

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
     * Array for vertices coordinates.
     */
    private List<Float> vertices = new ArrayList<Float>();
    /**
     * Array for faces drawing order.
     */
    private List<Short> faces = new ArrayList<Short>();

    /**
     * Main constructor.
     *
     * @param objId Name of an .obj file to load.
     */
    public ObjModel(int objId) {
        processObj(objId);
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
                if (parts.countTokens() == 0)
                    continue;
                /* First token in the line is always a type of data */
                String type = parts.nextToken();

                /* Depending of data type we perform adequate action */
                if (type.equals(VERTEX)) {
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                } else if (type.equals(FACE)) {
                    faces.add((short)(Short.parseShort(parts.nextToken())-1));
                    faces.add((short)(Short.parseShort(parts.nextToken())-1));
                    faces.add((short)(Short.parseShort(parts.nextToken())-1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
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

    /**
     * Returns size of {@link #faces} array.
     *
     * @return Size of {@link #faces}.
     */
    public int getFacesSize() {
        return faces.size();
    }

}
