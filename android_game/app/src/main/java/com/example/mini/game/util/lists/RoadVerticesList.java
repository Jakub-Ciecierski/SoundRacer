package com.example.mini.game.util.lists;

import com.example.mini.game.audio.BumperAnalyser;
import com.example.mini.game.audio.analysis.Bump;
import com.example.mini.game.shapes.basic.RoadObstacle;
import com.example.mini.game.shapes.basic.RoadVertex;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;

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

    public synchronized float getHeight(float v) {
        int d = 0;
        while (d* GameBoard.TIME_UNIT_LENGTH < v) {
            d++;
        }
        return vertices.get(d*2).y;
    }

    public synchronized void keepInPosition() {
        int vertexCounter = 0;
        int d = 0;
        for (int i = 0; i < GameBoard.ROAD_VERTICES_PER_BORDER; i++) {
            vertices.get(d++).z = vertexCounter * GameBoard.TIME_UNIT_LENGTH;
            vertices.get(d++).z = vertexCounter * GameBoard.TIME_UNIT_LENGTH;
            vertexCounter++;
        }
    }

    /**
     * At the beginning we just create a road shape for further animation.
     */
    public void generateStartShape() {
        int vertexCounter = 0;
        for (int i = 0; i < GameBoard.ROAD_VERTICES_PER_BORDER; i++) {
            Bump bump = BumperAnalyser.getNextBumperObj();
            add(new RoadVertex(0.0f, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * vertexCounter, false));
            add(new RoadVertex(GameBoard.ROAD_WIDTH, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * vertexCounter, false));
            vertexCounter++;
        }
    }
}
