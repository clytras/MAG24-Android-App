package com.wiznet.mag24gr;

import android.os.Bundle;
import android.annotation.SuppressLint;

import java.util.LinkedList;

import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

@SuppressLint("NewApi")
//public class PullToRefreshList extends ListActivity {
public class PullToRefreshList extends ListFragment  {

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
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pulltorefreshlist, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < 20; i++) {
			adapter.add(new SampleItem("Sample List", android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);*/
		
        // Set a listener to be invoked when the list should be refreshed.
        /*((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                //new GetDataTask().execute();
            	
            	Log.i("UPDATING LIST", "list update");
            	
            	((PullToRefreshListView) getListView()).onRefreshComplete();
            }
        });*/
        
        //final ListActivity _this = this;
        /*final ListFragment _this = this;
        
        
        final ArrayAdapter<String> lstAdapter = new ArrayAdapter<String>(_this,
                android.R.layout.simple_list_item_1, mListItems);*/

        //setListAdapter(lstAdapter);
        
        //return;


	}

    /** Called when the activity is first created. */
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

    }*/
    
    /*@Override
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
    }*/

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
           // ((PullToRefreshListView) getListView()).onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};
}