package com.example.mini.game.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.mini.game.R;
import com.example.mini.game.logic.GlobalState;

import java.util.ArrayList;


public class LauncherActivity extends ActionBarActivity {

    private ListView musicList;
    private ListView chosenMusicList;
    private CustomSongAdapter musicAdapter;
    private CustomSongAdapter chosenMusicListAdapter;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private ArrayList<Song> chosenSongs = new ArrayList<Song>();
    private int screenHeight;
    private int screenWidth;
    //chosenSongsPrevPosition is used to store information about position in previous list (songs)
    private ArrayList<Integer> chosenSongsPrevPosition = new ArrayList<Integer>();
    private Song m_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight=display.getHeight();
        screenWidth=display.getWidth();
        // initialize the system
        GlobalState.initSystem();
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        final MusicChooser musicChooser = new MusicChooser(cursor);
        Song song;
        while((song = musicChooser.getNextSong()) != null){
            songs.add(song);
        }

        musicList = (ListView) findViewById(R.id.musicList);
        RelativeLayout musicListRelativeLayout = (RelativeLayout) findViewById(R.id.musicRelativeLayout);
        //musicListRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams(screenWidth*(75/100),500));
        //musicList.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        chosenMusicList = (ListView) findViewById(R.id.musicListChosen);
        musicAdapter = new CustomSongAdapter(this, songs);
        chosenMusicListAdapter = new CustomSongAdapter(this, chosenSongs);
        musicList.setAdapter(musicAdapter);
        chosenMusicList.setAdapter(chosenMusicListAdapter);
        musicAdapter.notifyDataSetChanged();
        //((BaseAdapter) musicList.getAdapter()).notifyDataSetChanged();
        //((BaseAdapter) chosenMusicList.getAdapter()).notifyDataSetChanged();
        chosenMusicListAdapter.notifyDataSetChanged();

        //starting width of listView with music files is set to 75% of screen width
        RelativeLayout musicRelativeLayout = (RelativeLayout) findViewById(R.id.musicRelativeLayout);
        float layoutWidth = (float)screenWidth * (75.0f/100.0f);
        musicRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams((int)layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT));

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                m_song = (Song) adapter.getAdapter().getItem(position);
                if( m_song==null)
                    Log.i("Getting song path","something went wrong Harry");
                else {
                    chosenSongs.add(m_song);
                    chosenSongsPrevPosition.add(position);
                    musicAdapter.removeItem(position);
                    chosenMusicListAdapter.notifyDataSetChanged();
                    musicAdapter.notifyDataSetChanged();

                }
            }});
        chosenMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                m_song = (Song) adapter.getAdapter().getItem(position);
                if( m_song==null)
                    Log.i("Getting song path","something went wrong Harry");
                else {
                    songs.add(chosenSongsPrevPosition.get(position),m_song);
                    chosenMusicListAdapter.removeItem(position);
                    chosenSongsPrevPosition.remove(position);
                    chosenMusicListAdapter.notifyDataSetChanged();
                    musicAdapter.notifyDataSetChanged();

                }
            }});

    }
    //button click passing chosen file path
    public void starGameButton_Click(View view)
    {
        if(m_song != null) {
            GlobalState.addSong(m_song);


            Log.i("Launcher","Starting intent");
            Intent intent = new Intent(view.getContext(), LoadingActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Chose file from listView");
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
    }

    public void chosenMusicLinearLayout_Click(View view){
        LinearLayout chosenMusicLinearLayout = (LinearLayout) findViewById(R.id.chosenMusicLinearLayout);
        chosenMusicLinearLayout.setVisibility(View.INVISIBLE);
        LinearLayout musicLinearLayout = (LinearLayout) findViewById(R.id.musicLinearLayout);
        musicLinearLayout.setVisibility(View.VISIBLE);
        RelativeLayout musicRelativeLayout = (RelativeLayout) findViewById(R.id.musicRelativeLayout);
        float layoutWidth = (float)screenWidth * (25.0f/100.0f);
        musicRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams((int)layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    public void musicLinearLayout_Click(View view){
        LinearLayout chosenMusicLinearLayout = (LinearLayout) findViewById(R.id.chosenMusicLinearLayout);
        chosenMusicLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout musicLinearLayout = (LinearLayout) findViewById(R.id.musicLinearLayout);
        musicLinearLayout.setVisibility(View.INVISIBLE);
        RelativeLayout musicRelativeLayout = (RelativeLayout) findViewById(R.id.musicRelativeLayout);
        float layoutWidth = (float)screenWidth * (75.0f/100.0f);
        musicRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams((int)layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    /*public void setLayout(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.listLayout);
        layout.setLayoutParams( new LinearLayout.LayoutParams(480,320));
    }

    public void widthTestButton_Click(View view){
        LinearLayout layout = (LinearLayout) findViewById(R.id.listLayout);
       layout.setLayoutParams( new LinearLayout.LayoutParams(480,320));
  }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



   /* public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
