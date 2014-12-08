package com.example.mini.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.example.mini.game.shapes.complex.Road;

import java.util.List;


public class MyActivity extends Activity implements SensorEventListener{
    private GLSurfaceView glSurfaceView;
    private SensorManager sensorManager;
    private Sensor rotationVector;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new CustomGlSurfaceView(this);
        setContentView(glSurfaceView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }
    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        Road.totalZTranslation = 0.0f;
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("ROTATION_VECTOR_SENSOR", "[0]: " + event.values[0] + " [1]: " + event.values[1]
                + " [2]: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
