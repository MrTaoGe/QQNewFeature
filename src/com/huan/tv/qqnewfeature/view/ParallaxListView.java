package com.huan.tv.qqnewfeature.view;

import com.huan.tv.qqnewfeature.util.ResetViewHeightAnimation;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;
/**
 * 具有头部视差效果的自定义Listview。
 * <p>主要是重写过滑动(overScrollBy)方法</p>
 * @author MrTaoge
 *
 */
public class ParallaxListView extends ListView {
	private ImageView iv_header;
	/**没有拉伸的时候，头view(这里是一个ImageView)的真实高度*/
	private int initHeight;
	/**拉伸以后,头view的高度*/
	private int maxHeight;
	public ParallaxListView(Context context) {
		this(context,null);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		
	}
	public void setParallaxView(ImageView iv_header){
		this.iv_header = iv_header;
		initHeight = iv_header.getHeight();
		//允许过度拉伸到的最大高度就是ImageView承载的图片本身的高度。
		maxHeight = iv_header.getDrawable().getIntrinsicHeight();
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,int scrollY, int scrollRangeX, int scrollRangeY,int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if(deltaY<0&&isTouchEvent){//滚动到头部以后接着滚动的判断。
			int newHeight = iv_header.getHeight()+Math.abs(deltaY)/3;//头部view的实时高度，可见的高度加上滑动的高度的绝对值。将滑动距离/3的目的在于形成视差效果，这样的话，实际拖动的距离要比拉伸出来的距离大，形成一种比较难往外拉的效果,亦即视差效果。
			if(newHeight>maxHeight)newHeight = maxHeight;
			iv_header.getLayoutParams().height = newHeight;
			iv_header.requestLayout();//改变控件的尺寸时，应该申请给该控件重新布局。
		}
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_UP:
			//抬起手指以后头view恢复到之前的高度。
			ResetViewHeightAnimation animation = new ResetViewHeightAnimation(iv_header, initHeight);
			startAnimation(animation);
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
}
