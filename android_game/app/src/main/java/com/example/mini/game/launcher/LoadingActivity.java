package com.example.mini.game.launcher;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.mini.game.GameActivity;
import com.example.mini.game.R;
import com.example.mini.game.logic.GlobalState;

public class LoadingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GIFView gifView = new GIFView(this,1);
        setContentView(gifView);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        startLoading();
    }

    private void startLoading() {
        GlobalState.loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GlobalState.createNextAudioAnalyser();
                GlobalState.createNextAudioPlayer();

                try {
                    Log.i("LoadingThread", "Going to Sleep");
                    if(!GlobalState.isAnalyserReadyToGo()) {
                        synchronized (GlobalState.loadingMutex) {
                            GlobalState.loadingMutex.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
/*                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                Log.i("LoadingThread", "Awaken");
                startGame();
            }
        });

        GlobalState.loadingThread.start();

    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
