package com.huan.tv.qqnewfeature;

import com.huan.tv.qqnewfeature.DrawHelperLayout.DragStatus;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
/**
 * 自定义主面板ViewGroup控件，处理触摸事件。
 * @author MrTaoge
 *
 */
public class MainBoardLayout extends RelativeLayout {
	private DrawHelperLayout drawHelperLayout;
	
	

	public MainBoardLayout(Context context) {
		this(context,null);
	}

	public MainBoardLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public MainBoardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(drawHelperLayout.getStatus()==DragStatus.Close){
			return super.onTouchEvent(event);
		}else{
			if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_UP){
				drawHelperLayout.closeDrawer(true);
			}
			return true;
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(drawHelperLayout.getStatus()==DragStatus.Close){//屏蔽主面板打开时Listview依然可以滑动的事件,也就是说关闭的动作仍然按照父类的执行方式。
			return super.onInterceptTouchEvent(ev);
		}else{
			return true;
		}
	}
	
	public DrawHelperLayout getDrawHelperLayout() {
		return drawHelperLayout;
	}

	public void setDrawHelperLayout(DrawHelperLayout drawHelperLayout) {
		this.drawHelperLayout = drawHelperLayout;
	}
	
}
