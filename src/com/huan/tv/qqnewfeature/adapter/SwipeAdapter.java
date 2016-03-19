package com.huan.tv.qqnewfeature.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huan.tv.qqnewfeature.R;
import com.huan.tv.qqnewfeature.view.SwipeView;

public class SwipeAdapter extends BaseAdapter {
	private List<String> dataList;
	private Context context;
	public SwipeAdapter(Context context,List<String> dataList) {
		this.dataList = dataList;
		this.context = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_item, null);
		}
		holder = getViewHolder(convertView);
		holder.tv_content.setText("CONTENT-"+dataList.get(position));
		holder.tv_delete.setText("DELETE-"+dataList.get(position));
		return convertView;
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
