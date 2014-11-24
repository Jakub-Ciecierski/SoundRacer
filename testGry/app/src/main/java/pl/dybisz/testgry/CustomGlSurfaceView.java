package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import pl.dybisz.testgry.util.CustomCamera;
import pl.dybisz.testgry.util.EulerAnglesCamera;

/**
 * Created by dybisz on 2014-11-23.
 */
public class CustomGlSurfaceView extends GLSurfaceView {
    private float previousX;
    private float previousY;
    private float TOUCH_SCALE_FACTOR = 0.01f;
    /**
     *
     * @param context
     */
    public CustomGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new GameRenderer(context));
        /*
            Renders only when there is change to drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        */
    }

    /**
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {

//            case MotionEvent.ACTION_DOWN:
//                if(x < getWidth()/2) {
//                    Camera.rotateEyeXZPlane(0.1f);
//                    Log.i("CAMERA", "ROTATING LEFT");
//                }
//                else {
//                    Camera.rotateEyeXZPlane(-0.1f);
//                    Log.i("CAMERA", "ROTATING RIGHT");
//                }
//
//            default:
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

////                mRenderer.setAngle(
////                        mRenderer.getAngle() +
////                                ((dx + dy) * TOUCH_SCALE_FACTOR);  // = 180.0f / 320
////                requestRender();
                CustomCamera.rotate(dx * TOUCH_SCALE_FACTOR, dy * TOUCH_SCALE_FACTOR);

//                float mouseSensitivity = 10f;
//                EulerAnglesCamera.rotate(dx / mouseSensitivity, dy / mouseSensitivity);


        }

        previousX = x;
        previousY = y;
        return true;
    }
}
