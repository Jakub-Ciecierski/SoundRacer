package pl.dybisz.testgry.shapes;

/**
 * Created by user on 2014-11-23.
 */
public class CartesianCoordinates {
    private Line xLine;
    private Line yLine;
    private Line zLine;

    public CartesianCoordinates() {
        xLine = new Line(new float[]{1.0f, 0.0f, 0.0f, 1.0f},
                new float[]{-50f, 0f, 0f,
                        50f, 0f, 0f});
        yLine = new Line(new float[]{0f, 1.0f, 0f, 1.0f},
                new float[]{0f, -50f, 0f,
                        0f, 50f, 0f});
        zLine = new Line(new float[]{0.0f, 0.0f, 1.0f, 0.0f},
                new float[]{0f, 0f, -50f,
                        0f, 0f, 50f});
    }

    public void draw(float[] mMVPMatrix) {
        xLine.draw(mMVPMatrix);
        yLine.draw(mMVPMatrix);
        zLine.draw(mMVPMatrix);
    }
}
