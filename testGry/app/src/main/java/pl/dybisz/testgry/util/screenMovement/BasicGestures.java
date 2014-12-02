package pl.dybisz.testgry.util.screenMovement;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import pl.dybisz.testgry.GameRenderer;
import pl.dybisz.testgry.shapes.complex.SetOfButtons;
import pl.dybisz.testgry.util.MoveType;
import pl.dybisz.testgry.util.camera.DeveloperStaticSphereCamera;

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
        float switchCameraWidth = SetOfButtons.SWITCH_CAMERA_BUTTON_WIDTH_SCREEN_RATIO *
                SetOfButtons.screenWidth;
        switch (GameRenderer.currentCamera) {
            case DEVELOPER_CAMERA:
                /* Check if we tap inside left button area */
                if (e.getX() >= 0.0f && e.getX() <= buttonsWidth &&
                        e.getY() >= ((SetOfButtons.screenHeight - buttonsHeight) / 2) &&
                        e.getY() <= ((SetOfButtons.screenHeight + buttonsHeight) / 2)) {
                    DeveloperStaticSphereCamera.move(MoveType.MOVE_LEFT);
                }
                /* Check if we tap inside right button area */
                if (e.getX() >= SetOfButtons.screenWidth - buttonsWidth && e.getX() <= SetOfButtons.screenWidth &&
                        e.getY() >= ((SetOfButtons.screenHeight - buttonsHeight) / 2) &&
                        e.getY() <= ((SetOfButtons.screenHeight + buttonsHeight) / 2)) {
                    DeveloperStaticSphereCamera.move(MoveType.MOVE_RIGHT);
                }

                /* Check if we tap inside up button area */
                if (e.getY() >= SetOfButtons.screenHeight - buttonsHeight && e.getY() <= SetOfButtons.screenHeight &&
                        e.getX() >= ((SetOfButtons.screenWidth - buttonsWidth) / 2) &&
                        e.getX() <= ((SetOfButtons.screenWidth + buttonsWidth) / 2)) {
                    DeveloperStaticSphereCamera.move(MoveType.MOVE_UP);
                }


                /* Check if we tap inside down button area */
                if (e.getY() >= 0.0f && e.getY() <= buttonsHeight &&
                        e.getX() >= ((SetOfButtons.screenWidth - buttonsWidth) / 2) &&
                        e.getX() <= ((SetOfButtons.screenWidth + buttonsWidth) / 2)) {
                    DeveloperStaticSphereCamera.move(MoveType.MOVE_DOWN);
                }

                /* Check if we tap inside switch camera button area */

                if (e.getY() >= SetOfButtons.screenHeight - buttonsHeight && e.getY() <= SetOfButtons.screenHeight &&
                        e.getX() >= ((SetOfButtons.screenWidth - switchCameraWidth)) &&
                        e.getX() <= ((SetOfButtons.screenWidth))) {

                    GameRenderer.swapCameras();
                }
                break;
            case PLAYER_CAMERA:
                 /* Check if we tap inside switch camera button area */
                 if (e.getY() >= SetOfButtons.screenHeight - buttonsHeight && e.getY() <= SetOfButtons.screenHeight &&
                        e.getX() >= ((SetOfButtons.screenWidth - switchCameraWidth)) &&
                        e.getX() <= ((SetOfButtons.screenWidth))) {

                    GameRenderer.swapCameras();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        switch (GameRenderer.currentCamera) {
            case DEVELOPER_CAMERA:
                DeveloperStaticSphereCamera.rotate(distanceX
                        * TOUCH_SCALE_FACTOR, distanceY * TOUCH_SCALE_FACTOR);
                break;
            case PLAYER_CAMERA:
                break;
        }
        return true;
    }
}
