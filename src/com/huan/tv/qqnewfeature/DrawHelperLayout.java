package com.huan.tv.qqnewfeature;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrawHelperLayout extends FrameLayout {

	private ViewDragHelper dragHelper;
	/**左侧被覆盖的抽屉view*/
	private ViewGroup drawerView;
	/**最上层的主面板view*/
	private ViewGroup mainBoardView;
	private int measuredWidth;
	private int dragRange;
	private int measuredHeight;
	private DragStatusChangedListener dragStatusChangedListener;
	//初始状态是关闭状态。
	private DragStatus status = DragStatus.Close;

	/**
	 * 拖拽状态的枚举
	 * @author MrTaoge
	 */
	public static enum DragStatus{
		Open,Close,Draging
	}
	/**
	 * 拖拽状态发生改变时的监听。
	 * @author MrTaoge
	 *
	 */
	public interface DragStatusChangedListener{
		void open();
		void close();
		void draging(float fraction);
	}
	
	public void setDragStatusChangedListener(DragStatusChangedListener dragStatusChangedListener) {
		this.dragStatusChangedListener = dragStatusChangedListener;
	}
	
	public DrawHelperLayout(Context context) {
		this(context,null);
	}

	public DrawHelperLayout(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public DrawHelperLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);//这里是调用的父类构造。
		dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			//是否可以拖拽当前的控件。
			@Override
			public boolean tryCaptureView(View childView, int pointerId) {
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
			//当前被捕获到拖拽事件或者位置重置事件的控件的位置发生移动的时候调用，一般决定控件如何移动以及移动的距离等。
			@Override
			public void onViewPositionChanged(View changedView, int left,int top, int dx, int dy) {
				super.onViewPositionChanged(changedView, left, top, dx, dy);
				int mainBoardLeftMargin = mainBoardView.getLeft();
				if(changedView==drawerView){//通过左侧抽屉控件的位移来控制主面板的移动，与此同时，屏蔽抽屉控件的移动。
					mainBoardLeftMargin = mainBoardLeftMargin+dx;
					drawerView.layout(0, 0, measuredWidth, measuredHeight);//左侧的抽屉面板不允许移动，也就是每次有移动事件时，都把它的位置重置，就形成了没有动的效果。
					mainBoardLeftMargin = restrictEdgeWhenDrag(mainBoardLeftMargin);
					mainBoardView.layout(mainBoardLeftMargin, 0, mainBoardLeftMargin+measuredWidth, 0+measuredHeight);
				}
				//分发拖拽事件
				
				dispatchDragEvent(mainBoardLeftMargin);
				
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
	
	/**
	 * 分发拖拽事件
	 * @param mainBoardLeftMargin 主面板左端距离页面的距离。
	 */
	protected void dispatchDragEvent(int mainBoardLeftMargin) {
		float motionPercent = mainBoardLeftMargin*1.0f/dragRange;//算出主面板在可拖拽范围内的水平方向上的移动百分比。
		playAnimation(motionPercent);
		//刷新拖拽过程中的状态。
		updateDragStatus(motionPercent);
	}
	/**
	 * 更新拖拽状态。
	 * @param motionPercent
	 */
	private void updateDragStatus(float motionPercent) {
		DragStatus lastStatus = status;//不断的获取上一次刷新时的状态。
		if(motionPercent==0){
			status = DragStatus.Close;
		}else if(motionPercent==1){
			status = DragStatus.Open;
		}else{
			status = DragStatus.Draging;
		}
		if(dragStatusChangedListener!=null){
			dragStatusChangedListener.draging(motionPercent);
		}
		if(status!=lastStatus&&dragStatusChangedListener!=null){
			if(status==DragStatus.Open){
				dragStatusChangedListener.open();
			}else if(status==DragStatus.Close){
				dragStatusChangedListener.close();
			}else{
				dragStatusChangedListener.draging(motionPercent);
			}
		}
	}
	/**
	 * 播放拖拽时的动画。
	 * @param motionPercent
	 */
	private void playAnimation(float motionPercent) {
		//主面板的缩放动画
		ViewHelper.setScaleX(mainBoardView, evaluate(motionPercent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mainBoardView, evaluate(motionPercent, 1.0f, 0.8f));
		//左侧抽屉的缩放动画
		ViewHelper.setScaleX(drawerView, evaluate(motionPercent, 0.5f, 1.0f));
		ViewHelper.setScaleY(drawerView, evaluate(motionPercent, 0.5f,1.0f ));
		//抽屉的透明度变化
		ViewHelper.setAlpha(drawerView, evaluate(motionPercent, 0.0f, 1.0f));
		//抽屉的位移动画
		ViewHelper.setTranslationX(drawerView, evaluate(motionPercent, - measuredWidth*0.5f, 0.0f));
		//设置背景的渐变
		getBackground().setColorFilter(evaluateColor(motionPercent,Color.BLACK,Color.TRANSPARENT),Mode.SRC_OVER);
	}
	/**
	 * 计算颜色值的渐变。
	 * @param motionPercent
	 * @param black
	 * @param transparent
	 * @return
	 */
	private int evaluateColor(float motionPercent, int startValue, int endValue) {
		int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(motionPercent * (endA - startA))) << 24) |
                (int)((startR + (int)(motionPercent * (endR - startR))) << 16) |
                (int)((startG + (int)(motionPercent * (endG - startG))) << 8) |
                (int)((startB + (int)(motionPercent * (endB - startB))));
	}
	
	/**
	 * 计算开始值与结束值之间的渐变。
	 * @param percent
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public float evaluate(float percent,Number startValue,Number endValue){
		float startFloat = startValue.floatValue();
        return startFloat + percent * (endValue.floatValue() - startFloat);
	}
	//类似于ViewDragHelper和Scroller这类有可以定义控件滚动事件的类来说，这个方法是应该重写的，否则控件虽然可以移动，但是像松手后可以自行平滑的移动一段距离的这种事件却不能执行。也就是说"打一鞭子走一下"的生硬效果，而不带有缓冲效果。
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(dragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);//相当于invalidate方法。
//			invalidate();
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
	 * 当对控件进行拖拽时，限制控件的边缘位置，也就是屏蔽类似于0和超越屏幕宽度的这些非法值。
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
