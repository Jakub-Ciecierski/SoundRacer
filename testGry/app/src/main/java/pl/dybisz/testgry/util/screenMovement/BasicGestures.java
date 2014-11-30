package pl.dybisz.testgry.util.screenMovement;

import android.view.GestureDetector;
import android.view.MotionEvent;

import pl.dybisz.testgry.util.MoveType;
import pl.dybisz.testgry.util.StaticSphereCamera;

/**
 * Created by user on 2014-11-25.
 */
public class BasicGestures extends GestureDetector.SimpleOnGestureListener {
    private static final float TOUCH_SCALE_FACTOR = 0.01f;

    @Override
    public boolean onDown(MotionEvent e) {
        float buttonsWidth = ((SetOfButtons.screenHeight > SetOfButtons.screenWidth) ?
                SetOfButtons.screenWidth : SetOfButtons.screenHeight) * (SetOfButtons.UP_BUTTON_WIDTH_SCREEN_RATIO);
        /* I assume that buttons are squares */
        float buttonsHeight = buttonsWidth;

        /* Check if we tap inside left button area */
        if (e.getX() >= 0.0f && e.getX() <= buttonsWidth &&
                e.getY() >= ((SetOfButtons.screenHeight - buttonsHeight) / 2) &&
                e.getY() <= ((SetOfButtons.screenHeight + buttonsHeight) / 2)) {
            StaticSphereCamera.move(MoveType.MOVE_LEFT);
        }
        /* Check if we tap inside right button area */
        if (e.getX() >= SetOfButtons.screenWidth - buttonsWidth && e.getX() <= SetOfButtons.screenWidth &&
                e.getY() >= ((SetOfButtons.screenHeight - buttonsHeight) / 2) &&
                e.getY() <= ((SetOfButtons.screenHeight + buttonsHeight) / 2)) {
            StaticSphereCamera.move(MoveType.MOVE_RIGHT);
        }

        /* Check if we tap inside up button area */
        if (e.getY() >= SetOfButtons.screenHeight - buttonsHeight && e.getY() <= SetOfButtons.screenHeight &&
                e.getX() >= ((SetOfButtons.screenWidth - buttonsWidth) / 2) &&
                e.getX() <= ((SetOfButtons.screenWidth + buttonsWidth) / 2)) {
            StaticSphereCamera.move(MoveType.MOVE_UP);
        }


        /* Check if we tap inside down button area */
        if (e.getY() >= 0.0f && e.getY() <= buttonsHeight &&
                e.getX() >= ((SetOfButtons.screenWidth - buttonsWidth) / 2) &&
                e.getX() <= ((SetOfButtons.screenWidth + buttonsWidth) / 2)) {
            StaticSphereCamera.move(MoveType.MOVE_DOWN);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        StaticSphereCamera.rotate(distanceX * TOUCH_SCALE_FACTOR, distanceY * TOUCH_SCALE_FACTOR);
        return true;
    }
}
