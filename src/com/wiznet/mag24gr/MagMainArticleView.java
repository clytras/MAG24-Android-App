package com.wiznet.mag24gr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.keyes.youtube.OpenYouTubePlayerActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wiznet.mag24gr.Globals.Keys;
import com.wiznet.mag24gr.controls.ZoomWebView;


@SuppressLint("SetJavaScriptEnabled")
public class MagMainArticleView extends SherlockActivity {
	
	/*class MyJavaScriptInterface   
	{  
	    public void showHTML(String html)  
	    {  
	    	Log.i("MyJavaScriptInterface::showHTML", html);
	    }  
	} */
	
	private LinearLayout mLoader;
	private ProgressBar mProgressBar;
	private ZoomWebView mWebView;
	private boolean mIsJSLoaded = false;
	private boolean mHasGallery = false;
	//private JSONArray mGalleryImages;
	private ArrayList<String> mGalleryImages = new ArrayList<String>();
	private String[] mImagesArray;
	private JSONObject mJSData;
	private int mPreTriggerImageIndex = -1;
	private ShareActionProvider mShareActionProvider;
	
	private String itemId = "";
	private String itemUrl = "";
	private String itemTitle = "";
	private String itemLink = "";

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_article_view);
		com.actionbarsherlock.app.ActionBar abs = getSupportActionBar();
		
		abs.setDisplayShowCustomEnabled(true);
		abs.setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.main_abs_titleview, null);
		
		OnClickListener abs_click_listener = new OnClickListener() {
			@Override
		    public void onClick(View v) {
				finish();
		    }
		};
		
		TextView abs_title = (TextView)v.findViewById(R.id.title);

		abs_title.setText(R.string.main_abs_title);
		abs_title.setOnClickListener(abs_click_listener);
		
		mProgressBar = (ProgressBar) findViewById(R.id.loaderPb);
		mLoader = (LinearLayout) findViewById(R.id.loader);

		abs.setCustomView(v);
		
		Bundle b = getIntent().getExtras();
		//String itemId = b.getString("itemid");
		itemId = b.getString("itemId");
		itemUrl = b.getString("itemUrl");
		itemTitle = b.getString("itemTitle");
		itemLink = b.getString("itemLink");
 
		mWebView = (ZoomWebView) findViewById(R.id.webViewMain);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);
		//mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "androidInt"); 
		
		mWebView.setWebViewClient(new WebViewClient () {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	String urlUrl = url,
            		   urlParams = "";
            	
            	try {
	            	urlUrl = url.substring(0, url.indexOf("?"));
	            	urlParams = url.substring(url.indexOf("?") + 1);
            	}
            	catch(Exception e) {}
            	
            	Map<String, String> params = getQueryMap(urlParams);

	            if(url.startsWith("http://www.youtube.com")){
	            	String videoId = params.get("v");
	            	
	            	Intent lVideoIntent = new Intent(null, Uri.parse("ytv://"+videoId), MagMainArticleView.this, OpenYouTubePlayerActivity.class);
	            	startActivity(lVideoIntent);

	            	return true;
	            }
	            else if(params.containsKey("type")) {
	            	if(params.get("type").equals("gal")) {
	            		launchGallery(Integer.parseInt(params.get("index")));
	            		return true;
	            	}
	            	else if(params.get("type").equals("item")) {
	            		try {
	            			itemLink = urlUrl;
	            			itemId = params.get("itemid");
	            			itemTitle = params.get("title");
	            			itemUrl = MagData.GetItemUrl(itemId);
	            			
	            			mWebView.loadUrl(itemUrl);
	            		}
	            		catch(Exception e) {
	            			
	            		}
	            		
	            		return true;
	            	}
	            }
	            
	            return false;
            }
            
		    @SuppressWarnings("deprecation")
			@Override  
		    public void onPageFinished(WebView view, String url) {  
		    	view.setInitialScale((int)(100*view.getScale()));
		    	//mWebView.loadUrl("javascript:getJsData()");
		    }  
        });

		mWebView.setWebChromeClient(new WebChromeClient() {            
		    @Override
		    public boolean onJsAlert(WebView view, final String url, String message,
		    		final android.webkit.JsResult result) {
		    	
		    	mHasGallery = false;
		    	mGalleryImages.clear();
		    	
		    	try {
		    		mJSData = new JSONObject(message);
		    		String jsResult = mJSData.getString("result");
		    		
		    		JSONObject jsData = new JSONObject(mJSData.getString("data")), //mJSData.getJSONObject("data"),
		    				item = jsData.getJSONObject("item");
					
					if(item.getBoolean("hasGallery")) {
						JSONArray images = item.getJSONObject("gallery").getJSONArray("images");
						
						if(images.length() > 0) {
							for(int i = 0; i < images.length(); i++) {
								mGalleryImages.add(images.getString(i));
							}
							
							mImagesArray = new String[mGalleryImages.size()];
							mImagesArray = mGalleryImages.toArray(mImagesArray);
							mGalleryImages.clear();
							mHasGallery = true;
						}
					}
					
				}
		    	catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	finally {
					mIsJSLoaded = true;
					
					if(mHasGallery && mPreTriggerImageIndex != -1) {
						launchGallery(mPreTriggerImageIndex);
					}
				}

		        result.cancel();
		        return true;
		    }
		    
			public void onProgressChanged(WebView view, int progress) 
			{
				if(progress < 100 && mLoader.getVisibility() == ProgressBar.GONE) {
					mLoader.setVisibility(View.VISIBLE);
				}
				
				mProgressBar.setProgress(progress);

				if(progress == 100) {
					mLoader.setVisibility(View.GONE);
				}
			}
		});
		
		mWebView.loadUrl(itemUrl);

		//mWebView.loadUrl(String.format("http://mag24.gr/app/ctrl.php?load=item&item_id=%s", itemId));
		//mWebView.loadUrl(String.format("http://mag24.gr/app/ctrl.php?load=item&item_id=%s", 2776)); // gallery+video item
 
	}
	
	public void launchGallery() {
		launchGallery(0);
	}
	
	public void launchGallery(int index) {
		if(mHasGallery) {
        	//String image = url.substring(0, url.indexOf("?"));
    		Intent intent = new Intent(MagMainArticleView.this, MagImagePagerView.class);

    	    //String[] images = new String[mGalleryImages.size()];
    	    //images = mGalleryImages.toArray(images);
    		
    		intent.putExtra(Keys.IMAGES, mImagesArray);
    		intent.putExtra(Keys.IMAGE_POSITION, index);
    		startActivity(intent);
		} else {
			mPreTriggerImageIndex = index;
		}
	}
	
	public static Map<String, String> getQueryMap(String query) {
		Map<String, String> map = new HashMap<String, String>();  
		try {
		    String[] params = query.split("&");  
		    
		    for (String param : params) {  
		        String name = param.split("=")[0];
		        String value = param.split("=")[1];
		        map.put(name, value);  
		    }  
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return map;
	}

    @Override
    public void onBackPressed() {
    	finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/** Inflating the current activity's menu with res/menu/items.xml */
        getSupportMenuInflater().inflate(R.menu.main_article_view_items, menu);

        /** Getting the actionprovider associated with the menu item whose id is share */
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();

        
        /** Getting the target intent */
        Intent intent = getDefaultShareIntent();
        
        /** Setting a share intent */       
        if(intent!=null)
        	mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);
    }

    /** Returns a share intent */
    private Intent getDefaultShareIntent() {    	
    	
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, itemTitle);
        //intent.putExtra(Intent.EXTRA_TEXT, "http://mag24.gr/news/topika/to-mag24-sas-stelnei-stin-arxontiki-andro-mpeite-stin-klirosi-entelos-dorean");
        intent.putExtra(Intent.EXTRA_TEXT, itemLink);
        //intent.putExtra(Intent.EXTRA_STREAM,"Sample Content 22 !!!");
        return intent;
    }
    
    /*public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // your title was clicked!
            	finish();
        }
        
        return true;
    }*/

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.inCustomView()) {
                mWebView.hideCustomView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
         super.onConfigurationChanged(newConfig);
    }*/
    
}
