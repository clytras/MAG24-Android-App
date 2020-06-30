package com.wiznet.mag24gr;

import android.app.Application;

public class MagApp extends Application {
	
	public MagDataLoader magDataLoader = null;
	public MagData magData = null;
	
    @Override
    public void onCreate() {
        // Here you could pull values from a config file in res/raw or somewhere else
        // but for simplicity's sake, we'll just hardcode values
    	magData = new MagData();
        super.onCreate();
    }
    
    public void reData() {
    	magDataLoader = new MagDataLoader(magData);
    }
}
