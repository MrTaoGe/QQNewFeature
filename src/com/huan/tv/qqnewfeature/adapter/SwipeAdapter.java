package com.huan.tv.qqnewfeature.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.tv.qqnewfeature.R;
import com.huan.tv.qqnewfeature.view.SwipeView;
import com.huan.tv.qqnewfeature.view.SwipeView.OnSwipeStatusChangedListener;
import com.huan.tv.qqnewfeature.view.SwipeView.SwipeStatus;

public class SwipeAdapter extends BaseAdapter {
	private List<String> dataList;
	private Context context;
	private List<SwipeView> swipeViewList;
	
	public SwipeAdapter(Context context,List<String> dataList) {
		this.dataList = dataList;
		this.context = context;
		swipeViewList = new ArrayList<SwipeView>();
	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	private ViewHolder holder;
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_item, null);
		}
		holder = getViewHolder(convertView);
		holder.tv_content.setText("CONTENT-"+dataList.get(position));
		holder.tv_delete.setText("DELETE-"+dataList.get(position));
//		holder.tv_content.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				if(swipeViewList.size()>0){
//					closeAllSwipeViews();
//				}
//			}
//		});
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				dataList.remove(position);
				notifyDataSetChanged();
				Toast.makeText(context, "第"+position+"个条目被删除", 0).show();
			}
		});
		holder.swipeView.setSwipeStatusChangedListener(new OnSwipeStatusChangedListener() {
			
			public void onSwipe(SwipeView swipeView) {
				if(!swipeViewList.contains(swipeView)){
					closeAllSwipeViews();
				}
				swipeViewList.add(swipeView);
			}
			
			public void onOpen(SwipeView openView) {
				if(swipeViewList!=null){//打开一个条目之前先判断是否有已经打开的，如果有已经打开的，那么先关闭之前打开的再开启新的，一次只能打开一个条目。
					if(swipeViewList.size()>0){
						int size = swipeViewList.size();
						for (int i = 0; i < size; i++) {
							if(swipeViewList.get(i)!=openView){//不能把当前刚打开的也关了。
								swipeViewList.get(i).close(true);
							}
						}
					}
					if(!swipeViewList.contains(openView)){//每打开一个条目就将它装进集合便于之后统一关闭，和上面的逻辑顺序不能互换，因为开始时没有打开的条目。
						swipeViewList.add(openView);
					}
				}
			}
			
			public void onClose(SwipeView closeView) {
				swipeViewList.remove(closeView);
			}
		});
		return convertView;
	}
	/**
	 * 一次性关闭所有已打开的条目。
	 */
	public void closeAllSwipeViews() {
		if(swipeViewList!=null){
			int size = swipeViewList.size();
			for (int i = 0; i < size; i++) {
				if(swipeViewList.get(i).getCurrentStatus()!=SwipeStatus.Close){
					swipeViewList.get(i).close(true);
				}
			}
		}
	}

	class ViewHolder{
		private TextView tv_content,tv_delete;
		private SwipeView swipeView;
		public ViewHolder(View convertView){
			tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
			swipeView = (SwipeView) convertView.findViewById(R.id.swipeview);
		}
	}
	private ViewHolder getViewHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}
}
