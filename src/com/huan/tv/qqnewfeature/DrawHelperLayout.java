package com.huan.tv.qqnewfeature;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrawHelperLayout extends FrameLayout {

	private ViewDragHelper dragHelper;
	private ViewGroup drawerView;
	private ViewGroup mainBoardView;
	private int measuredWidth;
	private int dragRange;
	private int measuredHeight;

	public DrawHelperLayout(Context context) {
		this(context,null);
	}

	public DrawHelperLayout(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public DrawHelperLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			//是否可以拖拽当前的控件。
			@Override
			public boolean tryCaptureView(View childView, int arg1) {
				return childView==drawerView||childView==mainBoardView;
			}
			
			//获取当前控件的拖拽范围(水平方向),用来决定横向移动的速度。
			@Override
			public int getViewHorizontalDragRange(View child) {
				return dragRange;
			}
			//控件拖拽结束后"钳制"到的位置，亦即停留的水平位置，拖拽控件时，这个值不断变化，从而不断的重绘控件，造成不断移位的效果。
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				if(child==mainBoardView){//只控制主面板的移动范围。
					return restrictEdgeWhenDrag(left);
				}
				return left;
			}

			@Override
			public void onEdgeTouched(int edgeFlags, int pointerId) {
				super.onEdgeTouched(edgeFlags, pointerId);
			}
			
			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
				super.onViewCaptured(capturedChild, activePointerId);
			}

			@Override
			public void onViewPositionChanged(View changedView, int left,int top, int dx, int dy) {
				super.onViewPositionChanged(changedView, left, top, dx, dy);
				int mainBoardLeftPadding = mainBoardView.getLeft();
				if(changedView==drawerView){
					mainBoardLeftPadding = mainBoardLeftPadding+dx;
					drawerView.layout(0, 0, measuredWidth, measuredHeight);//左侧的抽屉面板不允许移动，也就是每次有移动事件时，都把它的位置重置，就形成了没有动的效果。
					mainBoardLeftPadding = restrictEdgeWhenDrag(mainBoardLeftPadding);
					mainBoardView.layout(mainBoardLeftPadding, 0, mainBoardLeftPadding+measuredWidth, 0+measuredHeight);
				}
				invalidate();//兼容2.3版本的拖拽事件，否则在2.3版本上不能实现拖拽。
			}
			
			//当拖拽事件结束后调用的方法，一般在这里播放拖拽后的动画。
			@Override
			public void onViewReleased(View releasedChild, float xvel,float yvel) {
				super.onViewReleased(releasedChild, xvel, yvel);
				if(xvel>0){//水平移动速度大于0，打开抽屉。
					openDrawer(true);
				}else if(xvel==0&&releasedChild.getLeft()>dragRange*0.5f){
					openDrawer(true);
				}else{
					closeDrawer(true);
				}
			}
			
		});
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(dragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	/**
	 * 关闭抽屉
	 * @param isSmooth 是否平滑的关闭。
	 */
	protected void closeDrawer(boolean isSmooth) {
		if(isSmooth){
			if(dragHelper.smoothSlideViewTo(mainBoardView, 0, 0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			mainBoardView.layout(0, 0, measuredWidth, measuredHeight);
			
		}
	}
	/**
	 * 打开抽屉
	 * @param isSmooth 是否平滑的打开。
	 */
	protected void openDrawer(boolean isSmooth) {
		if(isSmooth){
			if(dragHelper.smoothSlideViewTo(mainBoardView, dragRange,0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			mainBoardView.layout(dragRange, 0, dragRange+measuredWidth, measuredHeight);
		}
	}

	/**
	 * 当对控件进行拖拽时，限制控件的边缘位置。
	 * @param left
	 * @return
	 */
	protected int restrictEdgeWhenDrag(int left) {
		if(left<0){
			left = 0;
		}else if(left>dragRange){
			left = dragRange;
		}
		return left;
	}

	//如何具体的处理触摸事件。
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			dragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	//让拖拉控件自己处理是否拦截当前的触摸事件的逻辑。
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();
		if(childCount<2){
			throw new IllegalStateException("you must support two children at least for this parentview");
		}
		if(!(getChildAt(0) instanceof ViewGroup)||!(getChildAt(1) instanceof ViewGroup)){
			throw new IllegalArgumentException("the children of the current view must be an instanceof ViewGroup");
		}
		drawerView = (ViewGroup) getChildAt(0);
		mainBoardView = (ViewGroup) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		measuredWidth = mainBoardView.getMeasuredWidth();
		measuredHeight = mainBoardView.getMeasuredHeight();
		dragRange = (int) (measuredWidth*0.6f);
	}
}
