package com.example.mini.game;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mini.game.shapes.complex.Road;


public class MyActivity extends Activity {
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new CustomGlSurfaceView(this);
        setContentView(glSurfaceView);


    }
    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        Road.rememberMyPlx = 0.0f;
    }

    @Override
    protected void onResume() {
        super.onResume();
     }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
