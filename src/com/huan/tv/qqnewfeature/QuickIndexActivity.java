package com.huan.tv.qqnewfeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.huan.tv.qqnewfeature.adapter.QuickIndexAdapter;
import com.huan.tv.qqnewfeature.bean.FriendInfo;
import com.huan.tv.qqnewfeature.view.QuickIndexView;
import com.huan.tv.qqnewfeature.view.QuickIndexView.onLetterTouchedChangedListener;

public class QuickIndexActivity extends Activity{

	private QuickIndexView quickIndexView;
	private ListView listview_names;
	private QuickIndexAdapter adapter;
	private TextView tv_letter_selected;
	private List<FriendInfo> infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.acitivty_quickindex);
		initData();
		initView();
		
	}
	private void initView() {
		listview_names = (ListView) findViewById(R.id.listview_quickindex);
		quickIndexView = (QuickIndexView) findViewById(R.id.quickIndexView1);
		tv_letter_selected = (TextView) findViewById(R.id.tv_letter_selected);
		if(adapter==null){
			adapter = new QuickIndexAdapter(infos, this);
		}else{
			adapter.notifyDataSetChanged();
		}
		listview_names.setAdapter(adapter);
		listview_names.setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					
				}
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		quickIndexView.setOnLetterTouchedChangedListener(new onLetterTouchedChangedListener() {
			
			public void onTouchChanged(String letter) {
				Log.e("tag", letter);
				//当手在右边导航栏滑动时，将选中的字母索引展示在页面中央。
				showLetterSelected(letter);
				//遍历所有人名的拼音，取拼音的首写字母和右边选中的字母比较，找到一样的置顶。
				int size = infos.size();
				for (int i = 0; i < size; i++) {
					String firstLetter = infos.get(i).getPinyinIndex().charAt(0)+"";
					if(firstLetter.equals(letter)){
						listview_names.setSelection(i);//遍历到想要的结果后就停止遍历,将条目置顶。
						break;
					}
				}
			}
		});
	}
	private Handler handler = new Handler();
	protected void showLetterSelected(String letter) {
		tv_letter_selected.setVisibility(View.VISIBLE);
		tv_letter_selected.setText(letter);
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {
			
			public void run() {
				tv_letter_selected.setVisibility(View.GONE);
			}
		}, 2016);
		
	}
	private void initData() {
		if(infos==null){
			infos = new ArrayList<FriendInfo>();
		}
		infos.add(new FriendInfo("李伟"));
		infos.add(new FriendInfo("张三"));
		infos.add(new FriendInfo("阿三"));
		infos.add(new FriendInfo("阿四"));
		infos.add(new FriendInfo("段誉"));
		infos.add(new FriendInfo("段正淳"));
		infos.add(new FriendInfo("张三丰"));
		infos.add(new FriendInfo("陈坤"));
		infos.add(new FriendInfo("林俊杰1"));
		infos.add(new FriendInfo("陈坤2"));
		infos.add(new FriendInfo("王二a"));
		infos.add(new FriendInfo("林俊杰a"));
		infos.add(new FriendInfo("张四"));
		infos.add(new FriendInfo("林俊杰"));
		infos.add(new FriendInfo("王二"));
		infos.add(new FriendInfo("王二b"));
		infos.add(new FriendInfo("赵四"));
		infos.add(new FriendInfo("杨坤"));
		infos.add(new FriendInfo("赵子龙"));
		infos.add(new FriendInfo("杨坤1"));
		infos.add(new FriendInfo("李伟1"));
		infos.add(new FriendInfo("宋江"));
		infos.add(new FriendInfo("宋江1"));
		infos.add(new FriendInfo("李伟3"));
		Collections.sort(infos);
	}
}
