package com.example.mini.game.gameMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.example.mini.game.R;
import com.example.mini.game.launcher.GIFView;
import com.example.mini.game.logic.GlobalState;
import com.example.mini.game.util.enums.MoveType;
import com.example.mini.game.util.screenMovement.ShipMovement;

public class GameSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        ImageView img = (ImageView)findViewById(R.id.settingsActivityImageView);
        img.setBackgroundResource(R.drawable.speaker_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        /*
        getting setting from shared preferences
         */
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        int sensitivity = prefs.getInt("movementSensitivity",-1);
        int controller = prefs.getInt("controllerType",-1);
        if(sensitivity != -1){
            SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarTouch);
            seekBar.setProgress(sensitivity);
        }
        if(controller == 1){
            RadioButton radioButtonTouch = (RadioButton)findViewById(R.id.touchScreenOn);
            radioButtonTouch.setChecked(true);

        }
        else if (controller == 2)
        {
            RadioButton radioButtonAccelerometer = (RadioButton) findViewById(R.id.accelerometerOn);
            radioButtonAccelerometer.setChecked(true);
        }
        /*
        joystic
         */
    }
    public void onBackPressed() {
    previousActivity();
    }
    public void previousActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_right_in,R.anim.do_nothing);
        finish();
    }
    public void applyButton_Click(View view){
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarTouch);
        int value = seekBar.getProgress();
        Float tmp =0.075f + (0.1f*((float)value/100));
        ShipMovement.movementSensitivity = tmp;
        /*
                Saving ship movement sensitivity to shared preferences
                 */
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("movementSensitivity",value);
        editor.apply();
        //
        Log.i("GameSettingsActivity",Integer.toString(seekBar.getProgress()));
        Log.i("GameSettingsActivity",Float.toString(tmp));
        Intent intent = new Intent(view.getContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_right_in,R.anim.do_nothing);
        finish();
    }
    /*
    setting in game controller:
    -1- for touch
    -2- for accelerometer
    -3- for joystick
     */
    public void setAccelerometerControl(View view){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("controllerType",2);
        editor.apply();
        GlobalState.isTouch=false;

    }
    public void setTouchScreenControl(View view){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("controllerType",1);
        editor.apply();
        GlobalState.isTouch=true;
    }
}
