package com.wiznet.mag24gr;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        boolean isTopRow = (position == 0);
        
        //if(convertView==null)
        //	vi = inflater.inflate(isTopRow ? R.layout.list_top_row : R.layout.list_row, null);
        
        if(isTopRow)
        	vi = inflater.inflate(R.layout.list_top_row, null);
        else
        	vi = inflater.inflate(R.layout.list_row, null);

        
        TextView title = (TextView)vi.findViewById(R.id.title); // title
    	TextView artist = null, duration = null;

        /*if(!isTopRow) {
        	artist = (TextView)vi.findViewById(R.id.artist); // artist name
        	duration = (TextView)vi.findViewById(R.id.duration); // duration
        }*/
        
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        Log.i("Item position", String.valueOf(position));
        Log.i("isTopRow", String.valueOf(isTopRow));
        Log.i("Song", song.get(MainActivity.KEY_TITLE));
        
        // Setting all values in listview
        title.setText(song.get(MainActivity.KEY_TITLE));
        if(artist != null) {
        	artist.setText(song.get(MainActivity.KEY_ARTIST));
        	//artist.setText(">>> Your linear layout must have property orientation to be default ( not set) , try to set orientation = vertical, then you will see something new for what to tell and here is some more text to read for you and to read some more.");
        }
        
        if(duration != null) {
        	duration.setText(song.get(MainActivity.KEY_DURATION));
        }
        
        imageLoader.DisplayImage(song.get(MainActivity.KEY_THUMB_URL), thumb_image);
        return vi;
    }
}