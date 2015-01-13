package com.example.mini.game.gameMenu;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.example.mini.game.R;
import com.example.mini.game.util.screenMovement.ShipMovement;

public class GameSettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
    public void onBackPressed() {
    previousActivity();
    }
    public void backButton_Click(View view)
    {
        previousActivity();
    }
    public void previousActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        finish();
    }
    public void applyButton_Click(View view){
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarTouch);
        int value = seekBar.getProgress();
        Float tmp =0.075f + (0.1f*((float)value/100));
        ShipMovement.movementSensitivity = tmp;
        Log.i("GameSettingsActivity",Integer.toString(seekBar.getProgress()));
        Log.i("GameSettingsActivity",Float.toString(tmp));
        Intent intent = new Intent(view.getContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        finish();
    }
}
