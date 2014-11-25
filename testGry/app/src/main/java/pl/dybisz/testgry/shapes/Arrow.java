package pl.dybisz.testgry.shapes;

import static android.util.FloatMath.sqrt;

/**
 * Created by user on 2014-11-25.
 */
public class Arrow {
    private static float ARROW_HEAD_HEIGHT = 1f;
    private static float ARROW_HEAD_BASE = 0.3f;
    private float[] start;
    private float[] end;
    private float length;
    private float magnitude;
    private int programId;
    private float[] color;
    private Line line;
    private Cone arrowhead;

    public Arrow(float[] start, float[] end, float[] color, CartesianCoordinates.axes type, int programId) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.magnitude = calculateMagnitude(start, end);

        /* Line initialization */
        line = new Line(color,
                new float[]{start[0], start[1], start[2], end[0], end[1], end[2]},
                programId);

        /*
         * We check how much space of a vector magnitude takes the arrowhead.
         * Then (1 - ratio) tells us how much of a vector magnitude is not occupied
         * by the arrowhead. When we multiply every coordinate of the end point of the line
         * by this value, we get point in the middle of base of cone.
         * Call dybisz if You don't understand.
         * */
        float multiplier = (1 - ARROW_HEAD_HEIGHT / magnitude);
        float[] baseConePoint = new float[]{
                end[0] * multiplier,
                end[1] * multiplier,
                end[2] * multiplier
        };
        arrowhead = new Cone(ARROW_HEAD_BASE, ARROW_HEAD_HEIGHT, color, programId);
        applyTransformationOnArrowhead(type);
    }

    private void applyTransformationOnArrowhead(CartesianCoordinates.axes type) {
        switch (type) {
            case X_AXIS:
                arrowhead.translate(new float[]{magnitude, 0, 0}, new float[]{-90, 0, 0, 1});
                break;
            case Y_AXIS:
                arrowhead.translate(new float[]{0, magnitude, 0}, new float[]{0, 1, 0, 0});
                break;
            case Z_AXIS:
                arrowhead.translate(new float[]{0,0,magnitude}, new float[]{90,1,0,0});
                break;
        }
    }

    private float calculateMagnitude(float[] start, float[] end) {
        float dx = end[0] - start[0];
        float dy = end[1] - start[1];
        float dz = end[2] - start[2];
        return sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void draw(float[] projectionMatrix) {
        line.draw(projectionMatrix);
        arrowhead.draw(projectionMatrix);
    }
}
