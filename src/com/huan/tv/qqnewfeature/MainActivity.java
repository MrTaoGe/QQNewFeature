package com.huan.tv.qqnewfeature;

import java.util.Random;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.tv.qqnewfeature.DrawHelperLayout.DragStatusChangedListener;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

	private ListView listView_left,listView_main;
	private ImageView iv_head;
	private MainBoardLayout mainBoardLayout;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		iv_head = (ImageView) findViewById(R.id.iv_head_main);
		mainBoardLayout = (MainBoardLayout) findViewById(R.id.rl_main);
		listView_left = (ListView) findViewById(R.id.listView_left);
		listView_main = (ListView) findViewById(R.id.list_main);
		listView_left.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NamesSet.EnglishNames){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv_name = (TextView) super.getView(position, convertView, parent);
				tv_name.setTextColor(Color.WHITE);
				return tv_name;
			}
		});
		listView_main.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NamesSet.ChineseNames));
		DrawHelperLayout rootView = (DrawHelperLayout) findViewById(R.id.root_view);
		rootView.setDragStatusChangedListener(new DragStatusChangedListener() {
			
			public void open() {
				Toast.makeText(MainActivity.this, "打开", 0).show();
				listView_left.smoothScrollToPosition(new Random().nextInt(30));
			}
			
			public void draging(float fraction) {
//				Toast.makeText(MainActivity.this, "拖拽", 0).show();
				ViewHelper.setAlpha(iv_head, 1-fraction);
			}
			
			public void close() {
				Toast.makeText(MainActivity.this, "关闭", 0).show();
				ObjectAnimator animator = ObjectAnimator.ofFloat(iv_head, "translationX", 15f);
				animator.setInterpolator(new CycleInterpolator(2.0f));
				animator.setDuration(500);
				animator.start();
			}
		});
		
		mainBoardLayout.setDrawHelperLayout(rootView);
	}
}
