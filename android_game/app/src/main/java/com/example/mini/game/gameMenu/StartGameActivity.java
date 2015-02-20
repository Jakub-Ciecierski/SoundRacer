package com.example.mini.game.gameMenu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//import com.example.mini.game.MyActivity;
import com.example.mini.game.R;
import com.example.mini.game.launcher.LauncherActivity;

public class StartGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

    }

    @Override
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
        this.overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);
        finish();
    }
    public void singleSongButton_Click(View view)
    {
        Intent intent = new Intent(view.getContext(), LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_top_in,R.anim.push_top_out);
        finish();
    }
}
