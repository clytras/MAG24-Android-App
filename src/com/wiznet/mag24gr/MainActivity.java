package com.wiznet.mag24gr;

import android.os.Bundle;
import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@SuppressLint("NewApi")
public class MainActivity extends ListActivity {
//public abstract class MainActivity extends SlidingListActivity {

    private LinkedList<String> mListItems;

	ListView list;
    LazyAdapter adapter;

	// All static variables
	static final String URL = "http://api.androidhive.info/music/music.xml";
	// XML node keys
	static final String KEY_SONG = "song"; // parent node
	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_ARTIST = "artist";
	static final String KEY_DURATION = "duration";
	static final String KEY_THUMB_URL = "thumb_url";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setBehindContentView(R.layout.activity_main);

        /*SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu);*/
        
        // Set a listener to be invoked when the list should be refreshed.
        /*((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });*/
        
        PullToRefreshListView pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
        
        final ListActivity _this = this;
        //final SlidingListActivity _this = this;
        //final ArrayAdapter<String> lstAdapter = new ArrayAdapter<String>(_this,
        //        android.R.layout.simple_list_item_1, mListItems);

        //setListAdapter(lstAdapter);
        
        //return;

		final ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
        
		Thread mythread = new Thread() {
			public void run() {
				try {

			        mListItems = new LinkedList<String>();
			        mListItems.addAll(Arrays.asList(mStrings));

					XMLParser parser = new XMLParser();
					String xml = parser.getXmlFromUrl(URL); // getting XML from URL
					Document doc = parser.getDomElement(xml); // getting DOM element
					
					NodeList nl = doc.getElementsByTagName(KEY_SONG);
					// looping through all song nodes <song>
					for (int i = 0; i < nl.getLength(); i++) {
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						Element e = (Element) nl.item(i);
						// adding each child node to HashMap key => value
						map.put(KEY_ID, parser.getValue(e, KEY_ID));
						map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
						map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
						map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
						map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

						// adding HashList to ArrayList
						songsList.add(map);
					}
					
				} catch(Exception e) {}
				finally {
					
					runOnUiThread(new Runnable() {
		                @Override
		                public void run() {
							//list=(ListView)findViewById(R.id.list);
							
							// Getting adapter by passing xml data ArrayList
					        adapter=new LazyAdapter(_this, songsList);        
					        //list.setAdapter(adapter);
					        setListAdapter(adapter);
					        

					        // Click event for single list row
					        /*list.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
												

								}
							});		*/
		                }
					});

				}
			}
		};
		mythread.start();

    }
    
    @Override
    public void onBackPressed() {
    	//moveTaskToBack(true);

        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.mag24_exit)
	        .setMessage(R.string.mag24_exit_confirmation)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            finish();    
	        }
	
	    })
	    .setNegativeButton(android.R.string.no, null)
	    .show();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ;
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added after refresh...");

            // Call onRefreshComplete when the list has been refreshed.
            ////((PullToRefreshListView) getListView()).onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};
}