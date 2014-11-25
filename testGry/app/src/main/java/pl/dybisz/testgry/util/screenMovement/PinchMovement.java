package pl.dybisz.testgry.util.screenMovement;

import android.util.Log;
import android.view.ScaleGestureDetector;

import pl.dybisz.testgry.util.StaticSphereCamera;

/**
 * Created by user on 2014-11-25.
 */
public class PinchMovement extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float PINCH_SCALE_FACTOR = 5;
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = detector.getScaleFactor();
        StaticSphereCamera.setRadiusOfView(StaticSphereCamera.getRadiusOfView() +
        scale * ((scale >= 1) ? 1 : -1) / PINCH_SCALE_FACTOR);
        return true;
    }


}
