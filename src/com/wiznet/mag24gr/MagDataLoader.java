package com.wiznet.mag24gr;

import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class MagDataLoader extends AsyncTask<String, Void, Boolean> {
	private boolean displayProgress = false;
	public MagData magData;
	
	public boolean getDisplayProgress() {
		return this.displayProgress;
	}
	
	public void setDisplayProgress(boolean displayProgress) {
		this.displayProgress = displayProgress;
	}

	public MagDataLoader(MagData magData) {
		this.magData = magData;
	}

    public MagDataLoader(MagData magData, SlidingFragmentActivity activity) {
    	this.magData = magData;
        this.setActivity(activity);
    }

    public void setActivity(Object activity) {
        this.activity = activity;
        //dialog = new ProgressDialog(((Activity)activity).getBaseContext());
        dialog = new ProgressDialog((Context)activity);
    }

    /** progress dialog to show user that the backup is processing. */
    private ProgressDialog dialog;
    /** application context. */
    //private SlidingFragmentActivity activity = null;
    private Object activity = null;

    protected void onPreExecute() {
    	if(displayProgress && activity != null) { 
    		this.dialog.setMessage("Loading...");
        	this.dialog.show();
    	}
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(displayProgress && activity != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        //MessageListAdapter adapter = new MessageListAdapter(activity, titles);
        //setListAdapter(adapter);
        //adapter.notifyDataSetChanged();


        /*if(activity != null) { 
	        if(success) {
	            Toast.makeText((Activity)activity, "OK", Toast.LENGTH_LONG).show();
	        } else {
	            Toast.makeText((Activity)activity, "Error", Toast.LENGTH_LONG).show();
	        }
        }*/
    }

    protected Boolean doInBackground(final String... args) {
       try {    
          //BaseFeedParser parser = new BaseFeedParser();
          //messages = parser.parse();
          //List<Message> titles = new ArrayList<Message>(messages.size());
          //for (Message msg : messages){
          //    titles.add(msg);
          //}
          //activity.setMessages(titles);
    	   
    	  //Thread.sleep(7000);
    	   
    	   if(isNetworkAvailable()) {
	    	   magData.resetFlags();
	    	   magData.loadCategories();
	    	   
	    	   if(magData.hasSelectedCategory() && magData.selectedCategory.isAgenda())
	    		   magData.loadEvents();
	    	   else
	    		   magData.loadArticles();
    	   } else {
    		   Toast.makeText((Context)activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
    	   }
    	   
          return true;
       } catch (Exception e) {
          Log.e("tag", "error", e);
          return false;
       }
    }

    public boolean isNetworkAvailable() {
    	boolean result = false;
    	NetworkInfo activeNetworkInfo = null;

    	if(activity != null) {
	        ConnectivityManager connectivityManager 
	              = (ConnectivityManager) ((Activity)activity).getSystemService(Context.CONNECTIVITY_SERVICE);
	        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	        result = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    	}
    	return result;
    }
}
