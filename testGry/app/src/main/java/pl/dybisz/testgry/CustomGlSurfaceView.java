package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by dybisz on 2014-11-23.
 */
public class CustomGlSurfaceView extends GLSurfaceView {
    public CustomGlSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new GameRenderer(context));
        /*
            Renders only when there is change to drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        */
    }
}
