package com.example.mini.game.logic;

import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.mini.game.R;

public class CollisionActivity extends ActionBarActivity {

    /** The OpenGL View */
    private GLSurfaceView glSurface;

    /**
     * Initiate the OpenGL View and set our own
     * Renderer (@see Lesson03.java)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create an Instance with this Activity
        glSurface = new GLSurfaceView(this);
        glSurface.setEGLContextClientVersion(2);
        //Set our own Renderer
        glSurface.setRenderer(new Renderer(this));
        //Set the GLSurface as View to this Activity
        setContentView(glSurface);
    }

    /**
     * Remember to resume the glSurface
     */
    @Override
    protected void onResume() {
        super.onResume();
        glSurface.onResume();
    }

    /**
     * Also pause the glSurface
     */
    @Override
    protected void onPause() {
        super.onPause();
        glSurface.onPause();
    }
}
