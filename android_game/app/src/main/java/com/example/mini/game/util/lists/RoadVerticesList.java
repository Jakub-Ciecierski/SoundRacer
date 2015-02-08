package com.example.mini.game.util.lists;

import com.example.mini.game.shapes.basic.RoadVertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Class wraps list of {@link com.example.mini.game.shapes.basic.RoadVertex} and
 * provides small api for convenient usage.
 * <p></p>
 * Created by user on 2015-02-07.
 */
public class RoadVerticesList {
    /**
     * Main list. It holds current {@link com.example.mini.game.shapes.basic.RoadVertex vertices}
     * of {@link com.example.mini.game.shapes.complex.Road}
     */
    List<RoadVertex> vertices = new ArrayList<RoadVertex>();

    /**
     * @return {@link #vertices} as an array of type float filled with
     * x,y,z coordinates of each vertex.
     */
    public synchronized float[] asFloatArray() {
        float[] returnArray = new float[vertices.size() * 3];
        int i = 0;

        for (RoadVertex f : vertices) {
            returnArray[i++] = f.x;
            returnArray[i++] = f.y;
            returnArray[i++] = f.z;
        }
        return returnArray;
    }

    /**
     * Add an element to {@link #vertices}.
     *
     * @param roadVertex New element to add.
     */
    public synchronized void add(RoadVertex roadVertex) {
        vertices.add(roadVertex);
    }

    /**
     * Acquire element of index i.
     *
     * @param i Index of an element user wants to acquire.
     * @return Element of index i from {@link #vertices}.
     */
    public synchronized RoadVertex get(int i) {
        return vertices.get(i);
    }

    /**
     * Remove elements form interval <leftBoundary, rightBoundary).
     *
     * @param leftBoundary  Index which is left boundary of an interval to remove.
     * @param rightBoundary Index which is right boundary of an interval to remove.
     */
    public synchronized void remove(int leftBoundary, int rightBoundary) {
        vertices.subList(leftBoundary, rightBoundary).clear();
    }

    /**
     * Method sets new list as a current wrapped by this class.
     *
     * @param newList Fresh list to wrap.
     */
    public synchronized void set(ArrayList<RoadVertex> newList) {
        vertices = newList;
    }

    /**
     * Provides number of vertices in {@link #vertices}.
     *
     * @return Length of {@link #vertices}.
     */
    public synchronized  int length() {
        return vertices.size();
    }
}
