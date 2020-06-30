package com.wiznet.mag24gr;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class MainAbsTextView extends TextView {


	public MainAbsTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}


    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}