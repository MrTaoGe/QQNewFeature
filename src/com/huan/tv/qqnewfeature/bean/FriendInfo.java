package com.huan.tv.qqnewfeature.bean;

import com.huan.tv.qqnewfeature.util.PinYinUtil;

public class FriendInfo implements Comparable<FriendInfo>{
	private String name;
	/**拼音代表的索引*/
	private String pinyinIndex;
	public FriendInfo(String name) {
		this.name = name;
		setPinyinIndex(PinYinUtil.Chinese2PinYin(name));//避免多次调用这个转化方法。
	}
	
	public String getPinyinIndex() {
		return pinyinIndex;
	}
	public void setPinyinIndex(String pinyinIndex) {
		this.pinyinIndex = pinyinIndex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**通过姓名的拼音进行排序*/
	public int compareTo(FriendInfo another) {
		return this.pinyinIndex.compareTo(another.getPinyinIndex());
	}
}
