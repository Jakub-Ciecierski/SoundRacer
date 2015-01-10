package com.example.mini.game.gameMenu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.mini.game.R;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void settingsButton_Click(View view)
    {
            Intent intent = new Intent(view.getContext(), GameSettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
            finish();
    }
    public void startButton_Click(View view)
    {
        Intent intent = new Intent(view.getContext(), StartGameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_top_in,R.anim.push_top_out);
        finish();
    }

}
