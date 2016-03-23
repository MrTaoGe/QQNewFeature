package com.huan.tv.qqnewfeature.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
/**
 * 重置控件的高度时所添加的动画。
 * @author MrTaoge
 *
 */
public class ResetViewHeightAnimation extends Animation {
	/**控件所要恢复到初始高度值*/
	private int initHeight;
	private View targetView;
	/**控件要变化到什么高度*/
	private int targetHeight;
	/**需要播放动画的移动距离*/
	private int totalValue;
	public ResetViewHeightAnimation(View targetView,int targetHeight) {
		this.targetHeight = targetHeight;
		this.targetView = targetView;
		initHeight = targetView.getHeight();
		totalValue = targetHeight - initHeight;
		setDuration(500);
		setInterpolator(new OvershootInterpolator());//添加一个回弹效果。
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		int newHeight = (int) (initHeight+totalValue*interpolatedTime);
		targetView.getLayoutParams().height = newHeight;
		targetView.requestLayout();
	}

}
