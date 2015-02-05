package com.example.mini.game;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.AudioPlayer;
import com.example.mini.game.gameMenu.GameSettingsActivity;
import com.example.mini.game.gameMenu.MenuActivity;
import com.example.mini.game.launcher.GIFView;
import com.example.mini.game.launcher.LauncherActivity;
import com.example.mini.game.launcher.Song;
import com.example.mini.game.logic.GlobalState;
import com.example.mini.game.shapes.complex.Road;

import java.util.List;


public class GameActivity extends Activity implements SensorEventListener{
    protected CustomGlSurfaceView glSurfaceView;
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MyActivity","OnCreate has been started");
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getting Intent passed from LauncherActivity
        //Song song = (Song)getIntent().getExtras().getParcelable("song");

        super.onCreate(savedInstanceState);
        glSurfaceView = new CustomGlSurfaceView(this);


        setContentView(glSurfaceView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button startAudio = new Button(this);
        Button stopAudio = new Button(this);
        Button backToFileChooser = new Button(this);

        stopAudio.setText("Stop audio");
        startAudio.setText("Start audio");
        backToFileChooser.setText("Back");

        LinearLayout ll = new LinearLayout(this);

        ll.addView(startAudio);
        ll.addView(stopAudio);
        ll.addView(backToFileChooser);
        ll.setGravity(Gravity.LEFT | Gravity.RIGHT);
        this.addContentView(ll,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        startAudio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              glSurfaceView.onClickAudio();
            }
        });

        //stopAudio.setOnClickListener((v) -> { glSurfaceView.onClickstopAudio(); });

        stopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glSurfaceView.onClickstopAudio();
            }
        });

        backToFileChooser.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                        if(!AudioAnalyser.doneAnalysing){
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(v.getContext());
                            dlgAlert.setMessage("Please wait until the end of audio analysing");
                            dlgAlert.setTitle("lel");
                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dismiss the dialog
                                        }
                                    });
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                        else{
                          //  AudioPlayer.doneDecoding=true;
                         Intent intent = new Intent(v.getContext(), LauncherActivity.class);
                         startActivity(intent);
                         glSurfaceView.onClickstopAudio();
                         finish();
                     }
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
    public void onBackPressed() {
        this.glSurfaceView.gameRenderer.gameRunning = false;
        GlobalState.pauseAudio();

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Do you want to exit the game ?");
        dlgAlert.setTitle("Exit");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        returnToMenu();
                    }
                });
        dlgAlert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        glSurfaceView.gameRenderer.gameRunning = true;
                        GlobalState.playAudio();
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void returnToMenu(){
        /*Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        GlobalState.shutDownSystem();
        finish();*/

        Intent mStartActivity = new Intent(this, MenuActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        GlobalState.shutDownSystem();
        System.exit(0);
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
