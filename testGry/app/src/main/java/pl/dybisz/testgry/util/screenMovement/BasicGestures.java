package pl.dybisz.testgry.util.screenMovement;

import android.view.GestureDetector;
import android.view.MotionEvent;

import pl.dybisz.testgry.util.StaticSphereCamera;

/**
 * Created by user on 2014-11-25.
 */
public class BasicGestures extends GestureDetector.SimpleOnGestureListener {
    private static final float TOUCH_SCALE_FACTOR = 0.01f;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        StaticSphereCamera.rotate(distanceX * TOUCH_SCALE_FACTOR, distanceY * TOUCH_SCALE_FACTOR);
        return true;
    }
}
