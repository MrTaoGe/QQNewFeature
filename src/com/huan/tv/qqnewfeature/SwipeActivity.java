package com.huan.tv.qqnewfeature;

import java.util.ArrayList;
import java.util.List;

import com.huan.tv.qqnewfeature.adapter.SwipeAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SwipeActivity extends Activity {
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
	}

}
