package com.huan.tv.qqnewfeature;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.huan.tv.qqnewfeature.adapter.SwipeAdapter;
import com.huan.tv.qqnewfeature.view.SwipeView;

public class SwipeActivity extends Activity{
	private List<String> dataList = new ArrayList<String>();
	private ListView listView;
	private SwipeAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe2);
		listView = (ListView) findViewById(R.id.listview_swipe);
		for(int i=0; i<30; i++){
			dataList.add(""+i);
		}
		if(adapter==null){
			adapter = new SwipeAdapter(this, dataList);
		}else{
			adapter.notifyDataSetChanged();
		}
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					adapter.closeAllSwipeViews();//滑动listview时关闭所有已经打开的条目。
				}
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
}
