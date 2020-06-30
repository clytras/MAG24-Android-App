package com.wiznet.mag24gr;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.wiznet.mag24gr.MagMainContentList.MagAgendaEventItem;
import com.wiznet.mag24gr.MagMainContentList.MagMainContentItem;
import com.wiznet.mag24gr.controls.ZoomWebView;

@SuppressLint("NewApi")
public class ExMainActivity extends BaseActivity {
    MagMainContentList listadp;
    LazyAdapter adapter;
    PullToRefreshListView pullToRefreshView;
    ZoomWebView webView;
    //WebView webView;
    //LoadingWebViewClient webViewLoading;

	private CanvasTransformer mTransformer;
	boolean mIsRefreshing = false;
	
	public ExMainActivity(int titleRes, CanvasTransformer transformer) {
		super(titleRes);
		mTransformer = transformer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pulltorefreshlist);

		/*getSupportFragmentManager()
		.beginTransaction()
		//.replace(R.id.content_frame, new SampleListFragment())
		.replace(R.id.content_frame, pullToRefreshView)
		.commit();*/
		
		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		//webViewLoading = (LoadingWebViewClient) findViewById(R.id.webViewMainContainer);
		webView = (ZoomWebView)findViewById(R.id.webViewMain);
		//webView = webViewLoading.webView;

		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		    	Log.i("Refresh", "Refreshing the list");
		    	try {
					//Thread.sleep(2000);
		    		loadDataContent(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		
		pullToRefreshView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				
				final MagApp magApp = (MagApp)getApplicationContext();
				
				if(!magApp.magData.selectedCategory.isCinema() &&
					!magApp.magData.selectedCategory.isAgenda()) {

					showLoader(true, "header");
					magApp.magData.selectedCategory.offset--;
					loadDataContent(false);
				}
			}
		});

		pullToRefreshView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final MagApp magApp = (MagApp)getApplicationContext();
				
				//ListView actualListView = pullToRefreshView.getRefreshableView();
				
				//MagMainContentAdapter mmca = (MagMainContentAdapter)actualListView.getAdapter();

				//MagMainContentItem mmci = (MagMainContentItem)parent.getItemAtPosition(position); // mmca.getItem(position);
				
				if(!magApp.magDataLoader.isNetworkAvailable()) {
					Toast.makeText(magApp.getApplicationContext(), R.string.no_internet_found_check_connections, Toast.LENGTH_LONG).show();
				}
				else {
					Object item = parent.getItemAtPosition(position);
					
					if(item instanceof MagMainContentItem)
						triggerMainArtivleView((MagMainContentItem)item);
					else if(item instanceof MagAgendaEventItem)
						triggerAgendaEventView((MagAgendaEventItem)item);
					return;
				}
			}
		});

		listadp = new MagMainContentList();
		listadp.setActivity(this);
		ArrayAdapter<?> mmca = listadp.updateList();
    	/*ListView actualListView = pullToRefreshView.getRefreshableView();
    	actualListView.setAdapter(mmca);*/
		updateContentList(mmca, false);
		
		SlidingMenu sm = getSlidingMenu();
		setSlidingActionBarEnabled(true);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
		
		com.actionbarsherlock.app.ActionBar abs = getSherlock().getActionBar();
		
		abs.setDisplayShowCustomEnabled(true);
		abs.setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.main_abs_titleview, null);
		
		//LinearLayout ptrh = (LinearLayout)list.getView().findViewById(R.id.pull_to_refresh_header_wrp1);
		//ptrh.setVisibility(View.GONE);

		//if you need to customize anything else about the text, do it here.
		//I'm using a custom TextView with a custom font in my layout xml so all I need to do is set title
		
		OnClickListener abs_click_listener = new OnClickListener() {
			@Override
		    public void onClick(View v) {
				toggle();
		    }
		};
		
		
		TextView abs_title = (TextView)v.findViewById(R.id.title),
				 top_title = (TextView)findViewById(R.id.top_title);

		abs_title.setText(R.string.main_abs_title);
		
		abs_title.setOnClickListener(abs_click_listener);
		top_title.setOnClickListener(abs_click_listener);

		//assign the view to the actionbar
		//this.getActionBar().setCustomView(v);
		//this.getSherlock().setContentView(v);
		abs.setCustomView(v);
		//abs.setIcon(R.drawable.ic_mag_mainmenu);

		//magDataLoader.execute();
	}
	
	private void triggerMainArtivleView(MagMainContentItem item)
	{
		Intent intent = new Intent(ExMainActivity.this, MagMainArticleView.class);

		intent.putExtra("itemId", item.id);
		intent.putExtra("itemTitle", item.title);
		intent.putExtra("itemUrl", MagData.GetItemUrl(item.id));
		intent.putExtra("itemLink", item.articlelink);

		startActivity(intent);
	}
	
	private void triggerAgendaEventView(MagAgendaEventItem item)
	{
		Intent intent = new Intent(ExMainActivity.this, MagMainArticleView.class);

		//intent.putExtra("item", item);
		intent.putExtra("itemId", item.id);
		intent.putExtra("itemTitle", item.event);
		intent.putExtra("itemUrl", String.format("http://mag24.gr/app/ctrl.php?load=event&item_id=%s", item.id));
		intent.putExtra("itemLink", item.eventurl);
		//intent.putExtra("url", String.format("http://mag24.gr/app/ctrl.php?load=event&item_id=%s", item.id));

		////Intent intent = new Intent(ExMainActivity.this, MagAgendaEventView.class);

		//Bundle b = new Bundle();
		//b.putString("itemId", mmci.id);
		//intent.putExtra("eventContent", item.eventdescription);
		////intent.putExtra("eventid", item.id);

		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		//startActivityForResult(intent, 0);
		startActivity(intent);
	}
	
	public void loadDataContent() {
		loadDataContent(true);
	}
	
	public void loadDataContent(boolean displayProgress) {
		//MagDataLoader mdl = new MagDataLoader(this);
		//mdl.execute();
		
		if(mIsRefreshing)
			return;

		final MagApp magApp = (MagApp)getApplicationContext();
		
		if(!magApp.magDataLoader.isNetworkAvailable()) {
			Toast.makeText(magApp.getApplicationContext(), R.string.no_internet_found_check_connections, Toast.LENGTH_LONG).show();
        	pullToRefreshView.onRefreshComplete();
        	showLoader(false, "");
        	mIsRefreshing = false;
			return;
		}
		
		if(magApp.magData.hasSelectedCategory() && magApp.magData.selectedCategory.isCinema()) {
			toggleWebView(true);
			webView.loadUrl("http://mag24.gr/app/ctrl.php?load=cat&cat_id=cinema");
		} else {
			mIsRefreshing = true;
			magApp.reData();
			magApp.magDataLoader.setActivity(this);
			magApp.magDataLoader.setDisplayProgress(displayProgress);
			
			toggleWebView(false);
			webView.loadUrl("about:blank");
			
			Thread dataProc = new Thread() {
				public void run() {
					try {
						while(magApp.magDataLoader.getStatus() != Status.FINISHED) {
							sleep(100);
						}
					} catch(Exception e) {
						Log.e("ERROR!", e.toString());
					} finally {
						runOnUiThread(new Runnable() {
			                @Override
			                public void run() {
			                	ArrayAdapter<?> mmca = listadp.updateList();
			                	
			                	Log.i("isLoadingMore", Boolean.toString(listadp.isLoadingMore()));
			                	
			                	updateContentList(mmca, listadp.isLoadingMore());
			                	pullToRefreshView.onRefreshComplete();
			                	showLoader(false, "");
			                	mIsRefreshing = false;
			                }
						});
					}
				}
			};
			
			magApp.magDataLoader.execute();
			dataProc.start();
		}
	}
	
	private void toggleWebView(boolean showWebView) {
		pullToRefreshView.setVisibility(showWebView ? View.GONE : View.VISIBLE);
		webView.setVisibility(showWebView ? View.VISIBLE : View.GONE);
	}
	
	private void updateContentList(ArrayAdapter<?> adp, boolean justUpdate) {
		final MagApp magApp = (MagApp)getApplicationContext();
		
		Log.i("updateContentList", Integer.toString(adp.getCount()));
		
		try {
	    	if(!adp.isEmpty()) {
	    		if(justUpdate) {
	    			adp.notifyDataSetChanged();
	    		}
	    		else {
	    			ListView actualListView = pullToRefreshView.getRefreshableView();
	    			actualListView.setAdapter(adp);
	    		}
	    	}
	    	else {
	    		Toast.makeText(magApp.getApplicationContext(), R.string.couldnot_load_site, Toast.LENGTH_SHORT).show();
	    	}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
    
    public void showLoader(boolean show, String loading) {
    	
    	if(loading.equals("header")) {
    		TextView topTitle = (TextView)findViewById(R.id.top_title);
    		loading = topTitle.getText().toString();
    	}
    	
    	TextView t = (TextView)findViewById(R.id.loaderTitle);

    	t.setText("Loading: " + loading);
    	findViewById(R.id.loader).setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    public void showAbout() {
		Intent intent = new Intent(ExMainActivity.this, MagAboutView.class);
		startActivity(intent);
    }
}
