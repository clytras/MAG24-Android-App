package com.wiznet.mag24gr.controls;

import com.wiznet.mag24gr.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class LoadingWebViewClient extends RelativeLayout {
	
	private LayoutInflater inflater;
	private View layout;
	public WebView webView;
	public ProgressBar progressBar;

	public LoadingWebViewClient(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
    public LoadingWebViewClient(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    public LoadingWebViewClient(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView(context);
    }
    
    private void initView(Context context) {

		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.webview_loader, null); 
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
		//progressBar = (ProgressBar)layout.findViewById(R.id.webViewProgressBar);
		webView = (WebView)layout.findViewById(R.id.webViewMain);
        webView.setWebViewClient(new WebViewClient() {
            private int       webViewPreviousState;
            private final int PAGE_STARTED    = 0x1;
            private final int PAGE_REDIRECTED = 0x2;

            /*@Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                webViewPreviousState = PAGE_REDIRECTED;
                mWebView.loadUrl(urlNewString);
                return true;
            }*/

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                
               // progressBar.setVisibility(View.VISIBLE);
                /*webViewPreviousState = PAGE_STARTED;
                if (dialog == null || !dialog.isShowing())
                    dialog = ProgressDialog.show(WebViewActivity.this, "", getString(R.string.loadingMessege), true, true,
                            new OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // do something
                                }
                            });*/
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            	
            	//progressBar.setVisibility(View.GONE);

                /*if (webViewPreviousState == PAGE_STARTED) {
                    dialog.dismiss();
                    dialog = null;
                }*/

            }
        });
        
        webView.loadData("Testing!!", "text/html", "utf-8");
    }
    
    public WebView getWebView() {
    	return webView;
    }
}