package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import pl.dybisz.testgry.util.screenMovement.MovementController;

/**
 * Created by dybisz on 2014-11-23.
 */
public class CustomGlSurfaceView extends GLSurfaceView {
   MovementController movementController;


    /**
     * @param context
     */
    public CustomGlSurfaceView(Context context) {
        super(context);
        movementController = new MovementController(context);
        setEGLContextClientVersion(2);
        setRenderer(new GameRenderer(context));
        /*
            Renders only when there is change to drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        */
    }

    /**
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
           movementController.handleMovement(e);
           requestRender();
           return true;
    }
}
