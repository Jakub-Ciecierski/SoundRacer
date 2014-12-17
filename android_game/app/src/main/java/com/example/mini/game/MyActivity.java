package com.example.mini.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mini.game.launcher.LauncherActivity;
import com.example.mini.game.shapes.complex.Road;

import java.util.List;


public class MyActivity extends Activity implements SensorEventListener{
    private CustomGlSurfaceView glSurfaceView;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getting Intent passed from LauncherActivity
        String filePath = getIntent().getExtras().getString("filePath");

        super.onCreate(savedInstanceState);
        glSurfaceView = new CustomGlSurfaceView(this,filePath);
        setContentView(glSurfaceView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button startAudio = new Button(this);
        Button startAnalyzing = new Button(this);
        Button backToFileChooser = new Button(this);
        startAudio.setText("Start audio");
        startAnalyzing.setText("Start anal");
        backToFileChooser.setText("Back");

        LinearLayout ll = new LinearLayout(this);

        ll.addView(startAudio);
        ll.addView(startAnalyzing);
        ll.addView(backToFileChooser);
        ll.setGravity(Gravity.LEFT | Gravity.RIGHT);
        this.addContentView(ll,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        startAudio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              glSurfaceView.onClickAudio();
            }
        });

        startAnalyzing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               glSurfaceView.onClickAnal();
            }
        });

            backToFileChooser.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            Intent intent = new Intent(v.getContext(), LauncherActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
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
