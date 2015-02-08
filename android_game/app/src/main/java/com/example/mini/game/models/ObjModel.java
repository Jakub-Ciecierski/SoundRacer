package com.example.mini.game.models;

import android.util.Log;

import com.example.mini.game.CustomGlSurfaceView;
import com.example.mini.game.GameRenderer;
import com.example.mini.game.util.TexturesLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by user on 2015-02-08.
 */
public class ObjModel {
    //
    List<Integer> vertexIndices = new ArrayList<Integer>();
    List<Integer> uvIndices = new ArrayList<Integer>();
    List<Integer> normalIndices = new ArrayList<Integer>();
    // Temp
    List<Vec3> temp_vertices = new ArrayList<Vec3>();
    List<Vec2> temp_uvs = new ArrayList<Vec2>();
    List<Vec3> temp_normals = new ArrayList<Vec3>();
    // Out
    List<Vec3> out_vertices = new ArrayList<Vec3>();
    List<Vec2> out_uvs = new ArrayList<Vec2>();
    List<Vec3> out_normals = new ArrayList<Vec3>();

    private int textureId;

    public ObjModel(int objId, int texId) {
        processObj(objId);
        this.textureId = TexturesLoader.loadTexture(GameRenderer.context, texId);
    }

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


        // split data
        try {
            while ((line = buffer.readLine()) != null) {
                StringTokenizer parts = new StringTokenizer(line, " ");

                /* If line is empty skip it */
                int tokensCount = parts.countTokens();
                if (tokensCount == 0)
                    continue;

                /* First token in the line is always a type of data */
                String type = parts.nextToken();

                if (type.equals("v")) {
                    Vec3 vertex = new Vec3(Float.parseFloat(parts.nextToken()),
                            Float.parseFloat(parts.nextToken()),
                            Float.parseFloat(parts.nextToken()));
                    temp_vertices.add(vertex);
                } else if (type.equals("vt")) {
                    Vec2 uv = new Vec2(Float.parseFloat(parts.nextToken()),
                            1.0f - Float.parseFloat(parts.nextToken()));
                    temp_uvs.add(uv);
                } else if (type.equals("vn")) {
                    Vec3 normal = new Vec3(Float.parseFloat(parts.nextToken()),
                            Float.parseFloat(parts.nextToken()),
                            Float.parseFloat(parts.nextToken()));
                    temp_normals.add(normal);
                } else if(type.equals("f")) {
                    for(int i=0; i < 3 ; i++) {
                        StringTokenizer faceParts = new StringTokenizer(parts.nextToken(), "/");
                        // vertex
                        vertexIndices.add(Integer.parseInt(faceParts.nextToken()));
                        // texture/uv
                        uvIndices.add(Integer.parseInt(faceParts.nextToken()));
                        // normals
                        normalIndices.add(Integer.parseInt(faceParts.nextToken()));
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for(Vec3 v: temp_vertices){
            Log.i("TEMP_VERTICES", "x: " + v.x + " y: " + v.y + " z: " + v.z);
        }

        // process data
        ///////////////////////
        // vertices
        for(int i = 0; i < vertexIndices.size(); i++) {
            Log.i("VERTEX_INDICES","" + vertexIndices.get(i));
            Integer vertexIndex = vertexIndices.get(i);
            Vec3 vertex = temp_vertices.get(vertexIndex-1);
            out_vertices.add(vertex);
        }
        // uv's
        for(int i = 0; i < uvIndices.size(); i++) {
            Integer uvIndex = uvIndices.get(i);
            Vec2 uv = temp_uvs.get(uvIndex-1);
            out_uvs.add(uv);
        }
        // normals
        for(int i = 0; i < normalIndices.size(); i++) {
            Integer normalIndex = normalIndices.get(i);
            Vec3 normal = temp_normals.get(normalIndex-1);
            out_normals.add(normal);
        }
    }

    public float[] verticesAsFloats() {
       float[] floats = new float[out_vertices.size()*3];
        int d = 0;
        for(Vec3 v : out_vertices) {
            floats[d++] = v.x;
            floats[d++] = v.y;
            floats[d++] = v.z;
        }
        return floats;
    }
    public float[] uvAsFloats() {
        float[] floats = new float[out_uvs.size()*2];
        int d = 0;
        for(Vec2 uv : out_uvs) {
            floats[d++] = uv.x;
            floats[d++] = uv.y;
        }
        return floats;
    }

    public float[] normalsAsFloats() {
        float[] floats = new float[out_normals.size()*3];
        int d = 0;
        for(Vec3 n : out_normals) {
            floats[d++] = n.x;
            floats[d++] = n.y;
            floats[d++] = n.z;
        }
        return floats;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getVerticesSize() {
        return out_vertices.size()*3;
    }

    public int getUvsSize() {
        return out_uvs.size()*2;
    }

    public int getNormalsSize() {
        return out_normals.size()*3;
    }
}

class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Vec2 {
    public float x;
    public float y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }
}