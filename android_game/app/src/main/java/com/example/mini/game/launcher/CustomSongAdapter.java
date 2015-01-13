package com.example.mini.game.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mini.game.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Łukasz on 2015-01-10.
 */
public class CustomSongAdapter extends BaseAdapter implements View.OnClickListener {
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Song tempValues=null;
    int i=0;

    public CustomSongAdapter(Activity a, ArrayList d) {

        /********** Take passed values **********/
        activity = a;
        data=d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    public void removeItem(int position){data.remove(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public TextView songName;
        public TextView songAuthor;
        public TextView songDuration;
    }

    @Override
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.tabitem, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.songName = (TextView) vi.findViewById(R.id.songName);
            holder.songAuthor= (TextView) vi.findViewById(R.id.songAuthor);
            holder.songDuration = (TextView) vi.findViewById(R.id.songDuration);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.songName.setText("No Data");
            holder.songDuration.setText("");
            holder.songAuthor.setText("");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( Song ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.songName.setText( tempValues.getName());
            holder.songAuthor.setText(tempValues.getArtist());
            int duration = Integer.parseInt(tempValues.getDuration());
            int minutes = duration/60000;
            int seconds = (duration/1000)%60;
            String sec;
            if(seconds<10) {
                 sec = "0" + seconds;
            }
            else{
                sec = Integer.toString(seconds);
            }

            holder.songDuration.setText(""+minutes+":"+sec);
            /******** Set Item Click Listner for LayoutInflater for each row *******/

           // vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
    /********* Called when Item click in ListView ************/
    public static class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
        }
    }
}
