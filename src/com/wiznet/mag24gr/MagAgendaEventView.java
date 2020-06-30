package com.wiznet.mag24gr;

import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import com.wiznet.mag24gr.controls.ZoomWebView;


@SuppressLint("SetJavaScriptEnabled")
public class MagAgendaEventView extends Activity {
	
	private ZoomWebView mWebView;
	private boolean mIsJSLoaded = false;
	private JSONObject mJSData;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        //mWebView = new HTML5WebView(this);
        
        //if (savedInstanceState != null) {
        //	mWebView.restoreState(savedInstanceState);
        //} else {
    		//Bundle b = getIntent().getExtras();
    		//String itemId = b.getString("itemid");
    		
    		//mWebView.getSettings().setJavaScriptEnabled(true);
    		//mWebView.getSettings().setPluginState(PluginState.ON);
        	//mWebView.loadUrl(String.format("http://mag24.gr/app/ctrl.php?load=item&itemid=%s", itemId));;
            //mWebView.loadUrl("file:///data/bbench/index.html");
        //}
        
        //setContentView(mWebView.getLayout());
		
		setContentView(R.layout.main_article_view);
		
		Bundle b = getIntent().getExtras();
		//String eventContent = b.getString("eventContent");
		String eventId = b.getString("eventid");
 
		mWebView = (ZoomWebView) findViewById(R.id.webViewMain);
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		//mWebView.setWebChromeClient(new WebChromeClient() {
		//});

		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.setWebViewClient(new WebViewClient () {
            @ Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            
	            return true; // cancel all links
            }
            
		    /*@Override  
		    public void onPageFinished(WebView view, String url)  
		    {  
		    	mWebView.loadUrl("javascript:getJsData()");
		    }*/
        });

		//mWebView.loadDataWithBaseURL(null, eventContent, "text/html", "utf-8", null);
		mWebView.loadUrl(String.format("http://mag24.gr/app/ctrl.php?load=event&item_id=%s", eventId));
	}

    @Override
    public void onBackPressed() {
    	//moveTaskToBack(true);
    	
    	Log.i("onBackPressed", "on back pressed");
    	
    	finish();
    }
}
