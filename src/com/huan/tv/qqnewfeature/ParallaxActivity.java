package com.huan.tv.qqnewfeature;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.huan.tv.qqnewfeature.view.ParallaxListView;

public class ParallaxActivity extends Activity {
	private ParallaxListView listView;
	private View headView;
	private ImageView iv_header;
	private String[] datas = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_parallax);
		intiData();
		listView = (ParallaxListView) findViewById(R.id.parallaxListView1);
		listView.setOverScrollMode(AbsListView.OVER_SCROLL_NEVER);//去除过度滚动时头部出现的蓝色区域。
		headView = View.inflate(this, R.layout.parallax_header, null);
		iv_header = (ImageView) headView.findViewById(R.id.iv_parallax);
		//将视差view添加进listview的头部。
		headView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				listView.setParallaxView(iv_header);
				headView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		listView.addHeaderView(headView);//这个方法之后，测量控件的高度才有效。
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas));
	}
	private void intiData() {
		
	}

}
