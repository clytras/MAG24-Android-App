package com.wiznet.mag24gr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Splash extends Activity {

	private boolean splashActive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Hides the titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//ImageView iv = (ImageView) findViewById(R.id.mag_splash_logo);
		/*iv.getLayoutParams().width *= .7;
		iv.getLayoutParams().height *= .7;*/

		setContentView(R.layout.splash);
		RelativeLayout retry = (RelativeLayout)findViewById(R.id.reload_ph);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		
		retry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                doLoad(false);
            }
        });

		doLoad(true);
	}
	
	public void doLoad(boolean isFirstLoad) {
		final RelativeLayout retry = (RelativeLayout)findViewById(R.id.reload_ph);
		final ProgressBar pb = (ProgressBar)findViewById(R.id.splashProgressBar);
		final MagApp magApp = (MagApp)getApplicationContext();
		
		magApp.reData();
		magApp.magDataLoader.setActivity(this);
		magApp.magDataLoader.setDisplayProgress(false);

		if(!isFirstLoad) {
			retry.setVisibility(View.INVISIBLE);
			pb.setVisibility(View.VISIBLE);
		}
		
		Thread iNetCon = new Thread() {
			public void run() {
				try {
					sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					runOnUiThread(new Runnable() {
		                @Override
		                public void run() {
							pb.setVisibility(View.INVISIBLE);
							retry.setVisibility(View.VISIBLE);
		                }
					});
				}
			}
		};

		Thread dataProc = new Thread() {
			public void run() {
				try {
					while(magApp.magDataLoader.getStatus() != Status.FINISHED) {
						sleep(100);
					}

					splashActive = false;
				} catch(Exception e) {
					Log.e("ERROR!", e.toString());
				} finally {
					
					if(!splashActive) {
						//Intent intent = new Intent(Splash.this, MainActivity.class);
						Intent intent = new Intent(Splash.this, ExMainActivityAnim.class);
						//Intent intent = new Intent(Splash.this, ExMainActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
		};

		if(magApp.magDataLoader.isNetworkAvailable()) {
			magApp.magDataLoader.execute();
			dataProc.start();
		} else {
			iNetCon.start();
		}
	}
}