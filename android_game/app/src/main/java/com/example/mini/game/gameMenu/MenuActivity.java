package com.example.mini.game.gameMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mini.game.R;
import com.example.mini.game.launcher.GIFView;
import com.example.mini.game.launcher.LauncherActivity;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        GIFView gifView = new GIFView(this,1);
//        setContentView(gifView);
        setContentView(R.layout.activity_menu);
        ImageView img = (ImageView)findViewById(R.id.menuActivityImageView);
        img.setBackgroundResource(R.drawable.speaker_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

//        RelativeLayout lin = (RelativeLayout) findViewById(R.id.menuRelativeLayout);
//        lin.addView(gifView);

    }


    public void settingsButton_Click(View view)
    {
        Intent intent = new Intent(this, GameSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_left_in,R.anim.do_nothing);
        finish();
    }

    public void startButton_Click(View view)
    {
        Intent intent = new Intent(view.getContext(), LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_top_in,R.anim.do_nothing);
        finish();
    }

}
