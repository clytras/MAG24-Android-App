package com.wiznet.mag24gr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MagMainContentList {

	//public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	//	return inflater.inflate(R.layout.list, null);
	//}

	/*public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		updateList();
	}*/
	
	private Activity activity;
	private boolean mIsLoadingMore;
	MagApp magApp;
	
	public MagMainContentList() {
		//super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public Activity getActivity() {
		return this.activity;
	}
	
	public MagMainContentAdapter mainContentAdapter;
	public MagAgentaEventsAdapter agendaEventsAdapter;
	
	public ArrayAdapter<?> updateList() 
	{
		magApp = (MagApp)getActivity().getApplicationContext();

		if(magApp.magData.hasArticles)
			return updateContentList();
		else if(magApp.magData.hasEvents)
			return updateEventsList();
		
		return null;
	}
	
	public boolean isLoadingMore()
	{
		return mIsLoadingMore;
	}

	public MagMainContentAdapter updateContentList() {
		if(magApp.magData.hasArticles) {
			try {
				mIsLoadingMore = false;
				if(magApp.magData.hasSelectedCategory()) {
					if(magApp.magData.selectedCategory.offset == 0)
						mainContentAdapter = new MagMainContentAdapter(getActivity());
					else
						mIsLoadingMore = true;
				}
				else
					mainContentAdapter = new MagMainContentAdapter(getActivity());
				//int item_icon_id = 0;
				//boolean isHeaderItem = false;
				
	        	for(int i = 0; i < magApp.magData.magArticles.length(); i++)
	        	{
	        		JSONObject art = magApp.magData.magArticles.getJSONObject(i);
	        		
	        		MagMainContentItem mmci = new MagMainContentItem(art.getString("id"), art.getString("title"));
	        		
	        		mmci.introtext = art.getString("introtext");
	        		mmci.catID = art.getInt("catid");
	        		mmci.fulltext = art.getString("fulltext");
	        		mmci.articlelink = art.getString("articlelink");
	        		mmci.categorylink = art.getString("categorylink");
	        		
	        		try {
		        		JSONObject artimages = art.getJSONObject("images");
		        		Iterator<?> iimg = artimages.keys();
		        		
		        		while(iimg.hasNext()) {
		        			String imgkey = (String)iimg.next();
		        			mmci.images.put(imgkey, artimages.getString(imgkey));
		        		}
	        		} catch(JSONException je) {
	        			mmci.images.clear();
	        		}
        			
	        		mainContentAdapter.add(mmci);
	        	}
	
				//setListAdapter(adapter);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
		}
		
		return mainContentAdapter;
	}
	
	public MagAgentaEventsAdapter updateEventsList() {
		if(magApp.magData.hasEvents) {
			try {
				mIsLoadingMore = false;
				agendaEventsAdapter = new MagAgentaEventsAdapter(getActivity());
				
	        	for(int i = 0; i < magApp.magData.magEvents.length(); i++)
	        	{
	        		JSONObject evt = magApp.magData.magEvents.getJSONObject(i);

	        		MagAgendaEventItem maei = new MagAgendaEventItem(evt.getString("eid"), evt.getString("event"));

	        		maei.catname = evt.getString("catname");
	        		maei.venue = evt.getString("venue");
	        		maei.valias = evt.getString("valias");
	        		maei.eventstartdate = evt.getString("eventstartdate");
	        		maei.eventenddate = evt.getString("eventenddate");
	        		maei.eventstarttime = evt.getString("eventstarttime");
	        		maei.eventendtime = evt.getString("eventendtime");
	        		maei.ealias = evt.getString("ealias");
	        		maei.valias = evt.getString("valias");
	        		maei.eventdescription = evt.getString("eventdescription");
	        		maei.eventimage = evt.getString("eventimage");
	        		maei.author = evt.getString("author");
	        		maei.eventurl = evt.getString("eventurl");
	        		maei.displaydate = evt.getString("displaydate");
	        		maei.specialstartdate = evt.getString("specialstartdate");

	        		agendaEventsAdapter.add(maei);
	        	}
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
		}

		return agendaEventsAdapter;
	}
	
	public class MagMainContentItem implements Serializable {
		private static final long serialVersionUID = 8191695650985814129L;

		public String id;
		public String title;
		public String introtext;
		public int catID;
		public String fulltext;
		public long created;
		public long modified;
		public String created_by;
		public int hits;
		public String cat_title;
		public String author;
		public String authorname;
		public String slug;
		public String catslug;
		public HashMap<String, String> images;
		public String articlelink;
		public String categorylink;

		public MagMainContentItem(String id, String title) {
			this.id = id;
			this.title = title;
			this.images = new HashMap<String, String>();
		}
	}
	
	public class MagAgendaEventItem implements Serializable {
		private static final long serialVersionUID = 8763250557967482037L;

		public String id;
		public String event;
		public String catname;
		public String venue;
		public String valias;
		public String eventstartdate;
		public String eventenddate;
		public String eventstarttime;
		public String eventendtime;
		public String ealias;
		public String eventdescription;
		public String eventimage;
		public String author;
		public String eventurl;
		public String displaydate;
		public String specialstartdate;

		public MagAgendaEventItem(String id, String event) {
			this.id = id;
			this.event = event;
		}
	}

	public class MagMainContentAdapter extends ArrayAdapter<MagMainContentItem> {
	    public ImageLoader imageLoader; 
	    
		public MagMainContentAdapter(Context context) {
			super(context, 0);
			imageLoader = new ImageLoader(context);
		}
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	MagMainContentItem mmci = getItem(position);
	        boolean isTopRow = (position == 0);
	        String imgType;
	        
	        if(isTopRow) {
	        	convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_top_row, null);
	        	imgType = "m";
	        }
	        else {
	        	convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, null);
	        	imgType = "xs";
	        }
	        
	        TextView title = (TextView)convertView.findViewById(R.id.title); // title
	    	TextView introtext = (TextView)convertView.findViewById(R.id.introtext);
	        ImageView thumb_image = (ImageView)convertView.findViewById(R.id.list_image); // thumb image

	        title.setText(mmci.title.trim());
	        introtext.setText(Html.fromHtml(mmci.introtext).toString().trim());

	        try {
		        if(!mmci.images.isEmpty())
		        	imageLoader.DisplayImage(mmci.images.get(imgType), thumb_image);
		        else
		        	imageLoader.DisplayImage("drawable://no_image", thumb_image);
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        	imageLoader.DisplayImage("drawable://no_image", thumb_image);
	        }

	        return convertView;
	    }
	}

	public class MagAgentaEventsAdapter extends ArrayAdapter<MagAgendaEventItem> {
		public MagAgentaEventsAdapter(Context context) {
			super(context, 0);
		}
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	
	    	MagAgendaEventItem maei = getItem(position);
	        
	        if(convertView==null)
	        	convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_event_row, null);
	        
	        TextView title = (TextView)convertView.findViewById(R.id.title),
	        		 date = (TextView)convertView.findViewById(R.id.date),
	        		 venue = (TextView)convertView.findViewById(R.id.venue);
	        
	        title.setText(maei.event.trim());
	        date.setText(maei.displaydate.trim());
	        venue.setText(maei.venue.trim());
	        
	        if(maei.specialstartdate.equals("today"))
	        	date.setTextColor(Color.parseColor("#00AA00"));
	        else if(maei.specialstartdate.equals("tomorrow"))
	        	date.setTextColor(Color.RED);

	        return convertView;
	    }
	}
}
