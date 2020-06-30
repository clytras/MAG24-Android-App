package com.wiznet.mag24gr;

import android.graphics.Canvas;

//import com.slidingmenu.example.R;
//import com.slidingmenu.example.R.string;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class ExMainActivityAnim extends ExMainActivity {

	public ExMainActivityAnim() {
		// see the class CustomAnimation for how to attach 
		// the CanvasTransformer to the SlidingMenu
		super(R.string.app_name, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen*0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		});
	}

}
