package com.example.mini.game.launcher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.opengl.GLSurfaceView;
import com.example.mini.game.GameRenderer;
import com.example.mini.game.MyActivity;
import com.example.mini.game.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class LauncherActivity extends ActionBarActivity {

    private ListView musicList;
    private ArrayAdapter<Song> adapter;
    private List<Song> songNames = new ArrayList<Song>();
    private Song m_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
// Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

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
            songNames.add(song);
        }

        musicList = (ListView) findViewById(R.id.musicList);

        adapter = new ArrayAdapter<Song>(getApplicationContext(),  R.layout.custom_text_view, songNames);
        musicList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                m_song = (Song) adapter.getItemAtPosition(position);
                if( m_song==null)
                    Log.i("Getting song path","something went wrong Harry");
                else {
                    view.setSelected(true);
                }
            }});

    }
    //button click passing chosen file path
    public void starGameButton_Click(View view)
    {
        if(m_song != null) {
            Intent intent = new Intent(view.getContext(), MyActivity.class);


            intent.putExtra("song", (Parcelable) m_song);
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
