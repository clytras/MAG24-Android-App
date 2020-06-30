package com.wiznet.mag24gr;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.lang3.StringUtils;


public class MagMenuListFragment extends ListFragment {
//public class MagMenuListFragment extends PullToRefreshListFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		updateList();
	}
	
	public void updateList() {

		MagApp magApp = (MagApp)getActivity().getApplicationContext();
		
		if(magApp.magData.hasCategories) {
			try {
				MagMenuAdapter adapter = new MagMenuAdapter(getActivity());
				JSONArray cats;
				int item_icon_id = 0;
				boolean isHeaderItem = false;
				JSONObject cat = null,
							catArgs = null,
							params = null;
				
	        	for(int i = 0; i < magApp.magData.magCategories.length(); i++)
	        	{
	        		try {
		        		cat = magApp.magData.magCategories.getJSONObject(i);
		        		catArgs = null;
		        		params = cat.getJSONObject("params");
	        		}
	        		catch(JSONException aje1) {
	        			continue;
	        		}

	        		item_icon_id = 0;
	        		List<Integer> catIDs = new ArrayList<Integer>();

	        		try {
	        			catArgs = new JSONObject(cat.getString("note"));

	         			if(catArgs.has("icon"))
	        				item_icon_id = getResources().getIdentifier(catArgs.getString("icon"), "drawable", getActivity().getPackageName());

	         			if(catArgs.has("cats")) {
	         				cats = catArgs.getJSONArray("cats");
	         				for(int c = 0; c < cats.length(); c++) {
	         					catIDs.add(cats.getInt(c));
	         				}
	         			}
	        		}
	        		catch(JSONException aje2) {
	        		}
	        		
	        		String catLink = cat.getString("link"),
	        				catAlias;

	        		if(catLink.startsWith("magapp://")) {
	        			catAlias = catLink.substring(9);
	        		} else
	        			catAlias = cat.getString("alias");
	        		
	        		if(catIDs.size() == 0) {
	    				if(params.has("categories")) {
	         				cats = params.getJSONArray("categories");
	         				for(int c = 0; c < cats.length(); c++) {
	         					catIDs.add(cats.getInt(c));
	         				}
	    				}
	    				else {
	    					catIDs.add(0);
	    				}
	        		}
	        		
	        		isHeaderItem = cat.getString("type").equals("url");

	        		//Log.i("Loaded category ("+catAlias+"): "+ cat.getString("title"), StringUtils.join(catIDs, ","));
	        		
	        		MagMenuCategory mmc = new MagMenuCategory(
        					cat.getString("title"),
        					item_icon_id, 
        					isHeaderItem, 
        					cat.getInt("id"), 
        					catAlias,
        					cat.getJSONObject("params"),
        					catIDs);
        			
	        		if(!magApp.magData.hasSelectedCategory() && mmc.isHome())
	        			magApp.magData.selectedCategory = mmc;

        			adapter.add(mmc);
	        	}
	    		
	        	if(!adapter.isEmpty())
	        		setListAdapter(adapter);
	        	else
	        		Toast.makeText(magApp.getApplicationContext(), R.string.couldnot_load_site, Toast.LENGTH_SHORT).show();

	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
		}
	}

	public class MagMenuCategory {
		public String tag;
		public int iconRes;
		public boolean isHeaderItem;
		public List<Integer> catIDs;
		public int menuID;
		public String alias;
		public JSONObject params;
		public int offset;

		public MagMenuCategory(String tag, int iconRes, boolean isHeaderItem, int menuID, String alias, JSONObject params, List<Integer> catIDs) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.isHeaderItem = isHeaderItem;
			this.menuID = menuID;
			this.alias = alias.trim().toLowerCase();
			this.params = params;
			this.catIDs = catIDs;
			this.offset = 0;
		}

		public MagMenuCategory(String tag, int menuID, String alias, JSONObject params, List<Integer> catIDs) {
			this(tag, 0, false, menuID, alias, params, catIDs);
		}
		
		public boolean isHome() {
			return alias.equals("home");
		}
		
		public boolean isAgenda() {
			return alias.equals("agenda");
		}
		
		public boolean isCinema() {
			return alias.equals("cinema");
		}
		
		public boolean isNormalCategory() {
			return !(isHome() || isAgenda() || isCinema());
		}
		
		public boolean isSpecialContentCategory() {
			return isAgenda() || isCinema();
		}
	}

	public class MagMenuAdapter extends ArrayAdapter<MagMenuCategory> {

		public MagMenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			try {
				MagMenuCategory mmc = getItem(position);
				//boolean isTopRow = position <= 2;
				int rowRes = mmc.isHeaderItem ? R.layout.sliding_menu_top_row : R.layout.sliding_menu_cat_row;
				
				if(convertView == null) {
					//convertView = LayoutInflater.from(getContext()).inflate(R.layout.sliding_menu_top_row, null);
					convertView = LayoutInflater.from(getContext()).inflate(rowRes, null);
				}
				
				if(mmc.isHeaderItem) {
					ImageView icon = (ImageView)convertView.findViewById(R.id.row_icon);
					icon.setImageResource(mmc.iconRes);
				}
	
				TextView title = (TextView)convertView.findViewById(R.id.row_title);
				title.setText(mmc.tag);
			}
			catch(Exception e) {
			}

			return convertView;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    // TODO Auto-generated method stub
	    super.onListItemClick(l, v, position, id);
	    
	    MagApp magApp = (MagApp)getActivity().getApplicationContext();
	    magApp.magData.selectedCategory = (MagMenuCategory) this.getListView().getItemAtPosition(position);
	    magApp.magData.selectedCategory.offset = 0;
	    
	    String selectedItemText = ((TextView)v.findViewById(R.id.row_title)).getText().toString();
	    ExMainActivity acvt = (ExMainActivity)this.getActivity();
	    TextView topTitle = (TextView)acvt.findViewById(R.id.top_title);
	    topTitle.setText(selectedItemText);
	    
	    acvt.toggle();
	    acvt.loadDataContent();
	}
}
