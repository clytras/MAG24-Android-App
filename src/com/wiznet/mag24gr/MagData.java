package com.wiznet.mag24gr;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wiznet.mag24gr.MagMenuListFragment.MagMenuCategory;

import android.util.Log;

public class MagData {

	public JSONObject magDataResult = null;
	public JSONArray magCategories = null;
	public JSONArray magArticles = null;
	public JSONArray magEvents = null;

	public boolean hasCategories = false;
	public boolean hasArticles = false;
	public boolean hasEvents = false;
	
	public MagMenuCategory selectedCategory = null;
	
	public boolean hasSelectedCategory() {
		return selectedCategory != null;
	}
	
	public void resetSelectedCategory() {
		selectedCategory = null;
	}

	public class MagCategories
	{
		public static final String dataUrl = "http://mag24.gr/app/ctrl.php?load=cats";
		
		public static final String TAG_ID = "id";
		public static final String TAG_INTROTEXT = "introtext";
		public static final String TAG_FULLTEXT = "fulltext";
		public static final String TAG_TITLE = "title";
		public static final String TAG_CREATED = "created";
		public static final String TAG_MODIFIED = "modified";
		public static final String TAG_CATID = "catid";
		public static final String TAG_CREATEDBY = "created_by";
		public static final String TAG_HITS = "hits";
		public static final String TAG_CATTITLE = "cat_title";
		public static final String TAG_AUTHOR = "author";
		public static final String TAG_AUTHORNAME = "authorname";
		public static final String TAG_SLUG = "slug";
		public static final String TAG_CATSLUG = "catslug";
		public static final String TAG_IMAGES = "images";
		public static final String TAG_ARTICLELINK = "articlelink";
		public static final String TAG_CATEGORYLINK = "categorylink";
	}

	public static class MagArticles
	{
		public static final String dataUrl = "http://mag24.gr/app/ctrl.php?load=cat&cat_id=%s&offset=%s";
		
		public static String getDataUrl() {
			return getDataUrl("0", "0");
		}
		
		public static String getDataUrl(String catIDs) {
			return getDataUrl(catIDs, "0");
		}
		
		public static String getDataUrl(String catIDs, String offset) {
			return String.format(dataUrl, catIDs, offset);
		}
		
		public static final String TAG_ID = "id";
		public static final String TAG_TITLE = "title";
		public static final String TAG_ALIAS = "alias";
		public static final String TAG_NOTE = "note";
		public static final String TAG_LINK = "link";
		public static final String TAG_TYPE = "type";
	}
	
	public static class MagEvents
	{
		public static final String dataUrl = "http://mag24.gr/app/ctrl.php?load=cat&cat_id=agenda";
		
		public static final String TAG_ID = "eid";
		public static final String TAG_CATNAME = "catname";
		public static final String TAG_VENUE = "venue";
		public static final String TAG_VALIAS = "valias";
		public static final String TAG_EVENTSTARTDATE = "eventstartdate";
		public static final String TAG_EVENTENDDATE = "eventenddate";
		public static final String TAG_EVENTSTARTTIME = "eventstarttime";
		public static final String TAG_EVENTENDTIME = "eventendtime";
		public static final String TAG_EALIAS = "ealias";
		public static final String TAG_EVENT = "event";
		public static final String TAG_EVENTDESCRIPTION = "eventdescription";
		public static final String TAG_EVENTIMAGE = "eventimage";
		public static final String TAG_AUTHOR = "author";
		public static final String TAG_EVENTURL = "eventurl";
		public static final String TAG_DISPLAYDATE = "displaydate";
		public static final String TAG_SPECIALSTARTDATE = "specialstartdate";
	}

	public class MagDataParser
	{
		public InputStream is = null;
		public JSONObject jObj = null;
		public String json = "";
		
		public boolean result = false;
		public JSONObject data = null;
		public String error = "";
		public boolean hasError = false;
		
		public static final String TAG_RESULT = "result";
		public static final String TAG_DATA = "data";
		public static final String TAG_ERROR = "error";
		
		public static final String TAG_CATEGORIES = "categories";
		public static final String TAG_ARTICLES = "articles";
		public static final String TAG_EVENTS = "events";
	 
	    // constructor
	    public MagDataParser() {
	 
	    }
	 
	    public boolean getJSONFromUrl(String url) {
			result = false;
			data = null;
			error = "";
			
	        // Making HTTP request
	        try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(url);

	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	            return false;
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	            return false;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }

	        try {
	            StringBuilder sb = new StringBuilder();
	            byte[] bytes = new byte[1024];
	            int numRead = 0;
	            
	            while((numRead = is.read(bytes)) >= 0) {
	            	sb.append(new String(bytes, 0, numRead));
	            }

	            is.close();
	            json = sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	            return false;
	        }

	        // try parse the string to a JSON object
	        try {
	            jObj = new JSONObject(json);
	            
	            result = jObj.getBoolean(TAG_RESULT);
	            data = jObj.getJSONObject(TAG_DATA);
	            
	            hasError = jObj.has(TAG_ERROR);
	            if(hasError)
	            	error = jObj.getString(TAG_ERROR);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	            return false;
	        }

	        //return jObj;
	        return true;
	    }
	}

	public boolean loadArticles()
    {
		String cstIDs = "0",
				offset = "0";
		
		if(hasSelectedCategory()) {
			if(!selectedCategory.isHome())
				cstIDs = StringUtils.join(selectedCategory.catIDs, ",");
			
			offset = Integer.toString(selectedCategory.offset);
		}
    	
    	return loadArticles(cstIDs, offset);
    }

	public boolean loadArticles(String catIDs, String offset)
    {
    	hasArticles = false;
    	MagDataParser jParser = new MagDataParser();
    	
    	if(jParser.getJSONFromUrl(MagArticles.getDataUrl(catIDs, offset)))
    	{
	        try {
	            // Getting Object of Articles
	        	magArticles = jParser.data.getJSONArray(MagDataParser.TAG_ARTICLES);
	        	hasArticles = true;
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	    	return true;
    	}
    	else
    		return false;
    }
    
	public boolean loadCategories()
    {
    	hasCategories = false;
    	MagDataParser jParser = new MagDataParser();
    	
    	if(jParser.getJSONFromUrl(MagCategories.dataUrl))
    	{
	        try {
	            // Getting Object of Categories
	        	magCategories = jParser.data.getJSONArray(MagDataParser.TAG_CATEGORIES);
	        	hasCategories = true;

	            // Looping through all categories
	            /*for(int i = 0; i < contacts.length(); i++){
	                JSONObject c = contacts.getJSONObject(i);
	                 
	                // Storing each json item in variable
	                String id = c.getString(TAG_ID);
	                String name = c.getString(TAG_NAME);
	                String email = c.getString(TAG_EMAIL);
	                String address = c.getString(TAG_ADDRESS);
	                String gender = c.getString(TAG_GENDER);
	                 
	                // Phone number is agin JSON Object
	                JSONObject phone = c.getJSONObject(TAG_PHONE);
	                String mobile = phone.getString(TAG_PHONE_MOBILE);
	                String home = phone.getString(TAG_PHONE_HOME);
	                String office = phone.getString(TAG_PHONE_OFFICE);
	                 
	                // creating new HashMap
	                HashMap<String, String> map = new HashMap<String, String>();
	                 
	                // adding each child node to HashMap key => value
	                map.put(TAG_ID, id);
	                map.put(TAG_NAME, name);
	                map.put(TAG_EMAIL, email);
	                map.put(TAG_PHONE_MOBILE, mobile);
	 
	                // adding HashList to ArrayList
	                contactList.add(map);
	            }*/
	        	
	        	/*Iterator iter = magCategories.keys();
	        	while(iter.hasNext()){
	                String key = (String)iter.next();
	                String value = magCategories.getJSONObject(key).getString(MagCategories.TAG_TITLE);
	                //map.put(key,value);
	                Log.d("Cat", value);
	            }*/
	        	
	        	//JSONArray mCats = new JSONArray(); //magCategories.toJSONArray(names)
	        	
	        	//mCats.put(magCategories);
	        	
	        	//for(int i = 0; i < magCategories.length(); i++)
	        	//{
	        	//	Log.d("Cat", magCategories.getJSONObject(i).getString(MagCategories.TAG_TITLE));
	        	//}
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	    	return true;
    	}
    	else
    		return false;
    }
	
	public boolean loadEvents()
    {
		hasEvents = false;
    	MagDataParser jParser = new MagDataParser();
    	
    	if(jParser.getJSONFromUrl(MagEvents.dataUrl))
    	{
	        try {
	            // Getting Object of Events
	        	magEvents = jParser.data.getJSONArray(MagDataParser.TAG_EVENTS);
	        	hasEvents = true;

	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	    	return true;
    	}
    	else
    		return false;
    }
	
	public void resetFlags()
	{
		hasCategories = false;
		hasArticles = false;
		hasEvents = false;
	}
	
	public static String GetItemUrl(String itemId)
	{
		return String.format("http://mag24.gr/app/ctrl.php?load=item&item_id=%s", itemId);
	}
}
