
package com.huan.tv.qqnewfeature.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huan.tv.qqnewfeature.R;
import com.huan.tv.qqnewfeature.bean.FriendInfo;

public class QuickIndexAdapter extends BaseAdapter{
	
	private List<FriendInfo> infos;
	private Context context;
	public QuickIndexAdapter(List<FriendInfo> infos,Context context){
		this.context = context;
		this.infos = infos;
	}
	
	public int getCount() {
		return infos.size();
	}

	public Object getItem(int position) {
		return infos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	private ViewHolder holder;
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_quickindex_item, null);
		}
		holder = getHolder(convertView);
		FriendInfo friendInfo = infos.get(position);
		//通过相应索引下的名字转化成的拼音，截取拼音首字母作为索引的值。
		String pinYinIndex = friendInfo.getPinyinIndex().charAt(0)+"";
		if(position>0){
			String preLetter = infos.get(position-1).getPinyinIndex().charAt(0)+"";//和上一条数据的索引字母比较。
			if(!pinYinIndex.equals(preLetter)){
				holder.tv_letter_index.setText(pinYinIndex);
				holder.tv_letter_index.setVisibility(View.VISIBLE);
			}else{
				//把重复的条目去除就行。
				holder.tv_letter_index.setVisibility(View.GONE);
			}
		}else{
			holder.tv_letter_index.setText(pinYinIndex);
			holder.tv_letter_index.setVisibility(View.VISIBLE);
		}
		holder.tv_name.setText(friendInfo.getName());
		return convertView;
	}

	class ViewHolder{
		private TextView tv_letter_index,tv_name;
		public ViewHolder(View convertView){
			tv_letter_index = (TextView) convertView.findViewById(R.id.tv_index);
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		}
	}
	public ViewHolder getHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}

}
