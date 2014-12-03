package com.example.mini.game.shapes.basic;


import com.example.mini.game.shapes.complex.CartesianCoordinates;

import static android.util.FloatMath.sqrt;

/**
 * Class represents an arrow shape in the 3D space.
 * It consists of line and cone.
 * <p></p>
 * Created by dybisz on 2014-11-25.
 */
public class Arrow {
    /**
     * Constant describes default arrowhead height.
     */
    private static float ARROW_HEAD_HEIGHT = 1f;
    /**
     * Constant describes default arrowhead radius.
     */
    private static float ARROW_HEAD_RADIUS = 0.3f;
    /**
     * Start point of the arrow of the (x,y,z) form.
     */
    private float[] start;
    /**
     * End point of the arrow of the (x,y,z) form.
     */
    private float[] end;
    /**
     * Magnitude of the arrow. Can be calculated using {@link #calculateMagnitude(float[], float[])
     * calculateMagnitude()} method.
     */
    private float magnitude;
    /**
     * Color of the arrow.
     */
    private float[] color;
    /**
     * Line component of the arrow.
     * See {@link pl.dybisz.testgry.shapes.basic.Line} for more information.
     */
    private Line line;
    /**
     * Arrowhead component of the arrow.
     * See {@link pl.dybisz.testgry.shapes.basic.Cone} for more information.
     */
    private Cone arrowhead;

    /**
     * @param start     Start point of the arrow in the form: {x,y,z}.
     * @param end       End point of the arrow in the form: {x,y,z}.
     * @param color     Color of the arrow in the form: {r,g,b,a}.
     * @param type      Type of axis. See {@link pl.dybisz.testgry.shapes.complex.CartesianCoordinates.axes}.
     * @param programId Id of compiled openGL program.
     */
    public Arrow(float[] start, float[] end, float[] color, CartesianCoordinates.axes type, int programId) {
        /* Fill out fields */
        this.start = start;
        this.end = end;
        this.color = color;
        this.magnitude = calculateMagnitude(start, end);

        /* Line initialization */
        line = new Line(color,
                new float[]{start[0], start[1], start[2], end[0], end[1], end[2]},
                programId);

        /* Arrowhead initialization */
        arrowhead = new Cone(ARROW_HEAD_RADIUS, ARROW_HEAD_HEIGHT, color, programId);
        applyTransformationOnArrowhead(type);
    }

    /**
     * Method helps matching transformation of the arrowhead to proper axis.
     *
     * @param type Type of axis. See {@link pl.dybisz.testgry.shapes.complex.CartesianCoordinates.axes}.
     */
    private void applyTransformationOnArrowhead(CartesianCoordinates.axes type) {
        switch (type) {
            case X_AXIS:
                arrowhead.translate(new float[]{magnitude, 0, 0}, new float[]{-90, 0, 0, 1});
                break;
            case Y_AXIS:
                arrowhead.translate(new float[]{0, magnitude, 0}, new float[]{0, 1, 0, 0});
                break;
            case Z_AXIS:
                arrowhead.translate(new float[]{0, 0, magnitude}, new float[]{90, 1, 0, 0});
                break;
        }
    }

    /**
     * Calculation of the magnitude of the arrow using high-school method.
     *
     * @param start Start point of the arrow in the form: {x,y,z}.
     * @param end   End point of the arrow in the for: {x,y,z}.
     * @return Magnitude of the arrow.
     */
    private float calculateMagnitude(float[] start, float[] end) {
        float dx = end[0] - start[0];
        float dy = end[1] - start[1];
        float dz = end[2] - start[2];
        return sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Draw method. It simply calls drawing methods of {@link #line line}
     * and {@link #arrowhead arrowhead}.
     *
     * @param projectionMatrix Matrix used to project Arrow on the 3D scene.
     *                         Most commonly camera matrix.
     */
    public void draw(float[] projectionMatrix) {
        line.draw(projectionMatrix);
        arrowhead.draw(projectionMatrix);
    }
}
