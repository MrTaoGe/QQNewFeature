package com.huan.tv.qqnewfeature;

import com.huan.tv.qqnewfeature.view.StickyView;
import com.huan.tv.qqnewfeature.view.StickyView.DragMotionEventEndListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class StickyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		StickyView stickyView = new StickyView(this);
		stickyView.setOnDragMotionEventListener(new DragMotionEventEndListener() {
			
			public void onReset(boolean isOutOfRange) {
				Toast.makeText(StickyActivity.this, "返回了", 0).show();
			}
			
			public void onDisapper() {
				Toast.makeText(StickyActivity.this, "销毁了", 0).show();
			}
		});
		setContentView(stickyView);
	}
}
