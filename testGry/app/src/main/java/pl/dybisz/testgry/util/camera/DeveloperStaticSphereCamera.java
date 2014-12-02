package pl.dybisz.testgry.util.camera;

import android.opengl.Matrix;

import pl.dybisz.testgry.util.MoveType;
import pl.dybisz.testgry.util.mathematics.Vector3;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Class will represent camera object as a combined projection matrix
 * and view matrix.
 * Created by dybisz on 2014-11-24.
 */
public abstract class DeveloperStaticSphereCamera {
    private static float[] cameraMatrix = new float[16];

    private static Vector3 eyeCoordinates = new Vector3(0.1f, 3.0f, -20.0f);
    private static Vector3 lookAtCoordinates = new Vector3(0f, 0f, 0f);
    private static Vector3 perpendicularVector = new Vector3(0f, 0f, 0f);
    private static Vector3 upVector = new Vector3(0f, 1f, 0f);

    private static float xAngle = 0;
    private static float yAngle = 0;
    private static float radiusOfView = 20;


    public static void rotate(float dx, float dy) {
        eyeCoordinates.setX(radiusOfView * sin(xAngle + dx) * sin(yAngle + dy) + perpendicularVector.getX());
        eyeCoordinates.setY(radiusOfView * cos(yAngle + dy) + perpendicularVector.getY());
        eyeCoordinates.setZ(radiusOfView * sin(yAngle + dy) * cos(xAngle + dx) + perpendicularVector.getZ());

        xAngle += dx;
        yAngle += dy;
    }

    public static void move(MoveType moveType) {
        float leftRightModify = 1;
        switch (moveType) {
            case MOVE_RIGHT:
                perpendicularVector =
                        new Vector3(lookAtCoordinates.getZ() - eyeCoordinates.getZ(),
                                0, lookAtCoordinates.getX() + eyeCoordinates.getX());

                float kr = 1 / sqrt((perpendicularVector.getX()) * (perpendicularVector.getX())
                        + (perpendicularVector.getZ()) * (perpendicularVector.getZ()));

                perpendicularVector.multiplyBy(-kr);
                break;

            case MOVE_LEFT:
                perpendicularVector =
                        new Vector3(lookAtCoordinates.getZ() - eyeCoordinates.getZ(),
                                0, lookAtCoordinates.getX() + eyeCoordinates.getX());

                float k = 1 / sqrt((perpendicularVector.getX()) * (perpendicularVector.getX())
                        + (perpendicularVector.getZ()) * (perpendicularVector.getZ()));

                perpendicularVector.multiplyBy(k*leftRightModify);
                break;

            case MOVE_UP:
                perpendicularVector = new Vector3(0f, -1f, 0f);
                break;
            case MOVE_DOWN:
                perpendicularVector = new Vector3(0f,1f,0f);
                break;
        }

        eyeCoordinates.add(perpendicularVector);
        lookAtCoordinates.add(perpendicularVector);
    }

    public static float[] getCameraMatrix(float[] projectionMatrix) {
        float[] scratch = new float[16];
        updateMatrixInformation();
        Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, cameraMatrix, 0);
        return scratch;
    }

    /**
     * Every time renderer demands a matrix (each frame of onDraw()),
     * cameraMatrix will be updated to current settings.
     */
    private static void updateMatrixInformation() {
        Matrix.setLookAtM(cameraMatrix,
                0,
                eyeCoordinates.getX(),
                eyeCoordinates.getY(),
                eyeCoordinates.getZ(),
                lookAtCoordinates.getX(),
                lookAtCoordinates.getY(),
                lookAtCoordinates.getZ(),
                upVector.getX(),
                upVector.getY(),
                upVector.getZ());
    }

    public static float getRadiusOfView() {
        return radiusOfView;
    }

    public static void setRadiusOfView(float radiusOfView) {
        DeveloperStaticSphereCamera.radiusOfView = radiusOfView;
        // Just to update coordinates:
        rotate(0, 0);
    }
}
