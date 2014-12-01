package com.example.mini.game.audio;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mini.game.R;


import com.example.mini.game.graphview.GraphView;
import com.example.mini.game.graphview.GraphViewSeries;
import com.example.mini.game.graphview.LineGraphView;

import java.io.File;
import java.util.List;


public class AudioSampleActivity extends ActionBarActivity {
    // path to file
    //final String FILE = "/sdcard/external_sd/Music/Billy_Talent/Billy Talent - Diamond on a Landmine with Lyrics.mp3";
    //final String FILE = "/sdcard/external_sd/Music/Billy_Talent/judith.mp3";
    final String FILE = "/sdcard/external_sd/Music/Billy_Talent/explosivo.mp3";

    AudioAnalyser audioAnalyser;

    boolean isPaused = false;

    final int bufferSize = 4096;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_sample);

        audioAnalyser = new AudioAnalyser(FILE, bufferSize, 44100);
        AudioAnalyser.initAnalyser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pauseMusic(View view)  {
        if(!isPaused) {
            audioAnalyser.pauseAudio();
            isPaused = true;
        } else {
            audioAnalyser.playAudio();
            isPaused = false;
        }
    }

    public void rewindMusic(View view)  {
        audioAnalyser.rewindAudio(2000);
    }

    public void drawGraph(View view)  {
        List<Float> spectralFlux = audioAnalyser.analyzeEntireAudio();

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[spectralFlux.size()];
        for(int i = 0;i < data.length; i++) {
            float time = i*(bufferSize/2) * AudioAnalyser.MsPerSample;
            time = time / 1000; // in seconds
            data[i] = new GraphView.GraphViewData(time, spectralFlux.get(i));
        }
        GraphViewSeries exampleSeries = new GraphViewSeries(data);

        GraphView graphView = new LineGraphView(
                this // context
                , "Flux" // heading
        );
        graphView.addSeries(exampleSeries); // data

        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
        layout.addView(graphView);
    }

    public void startMusic(View view) throws Exception {
        audioAnalyser.analyzeAndWrite();
        audioAnalyser.playAudio();
    }
}
