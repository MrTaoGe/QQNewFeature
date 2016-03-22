package com.huan.tv.qqnewfeature.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
/**
 * 消息列表中单个条目(向左拖动会脱出隐藏的删除按钮等效果)
 * @author MrTaoge
 *
 */
public class SwipeView extends FrameLayout {

	private ViewDragHelper dragHelper;
	private View contentView,deleteView;
	private boolean hasOpen = false;
	private SwipeStatus swipeStatus = SwipeStatus.Close;
	private OnSwipeStatusChangedListener swipeStatusChangedListener;
	private int contentViewWidth;
	private int deleteViewWidth;
	private int deleteViewHeight;
	
	public SwipeView(Context context) {
		this(context,null);
	}

	public SwipeView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	/**滑动状态改变的枚举*/
	public enum SwipeStatus{
		Open,Close,Swiping
	}
	public SwipeStatus getCurrentStatus(){
		return swipeStatus;
	}
	
	
	/**滑动状态发生改变时的监听*/
	public interface OnSwipeStatusChangedListener{
		void onOpen(SwipeView openView);
		void onClose(SwipeView closeView);
		void onSwipe(SwipeView swipeView);
	}
	
	public void setSwipeStatusChangedListener(OnSwipeStatusChangedListener swipeStatusChangedListener){
		this.swipeStatusChangedListener = swipeStatusChangedListener;
	}
	
	public OnSwipeStatusChangedListener getOnSwipeStatusChangedListener(){
		return swipeStatusChangedListener;
	}
	private void initView() {
		dragHelper = ViewDragHelper.create(this, callback);
	}
	
	private ViewDragHelper.Callback callback = new Callback() {
		
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView||child==deleteView;
		}
		
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteViewWidth;
		}
		
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==contentView){
				if(left<-deleteViewWidth)left=-deleteViewWidth;
				if(left>0)left=0;
			}else{
				if(left<contentViewWidth-deleteViewWidth)left=contentViewWidth-deleteViewWidth;
				if(left>contentViewWidth)left = contentViewWidth;
			}
			return left;

		}
		
		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}
		//滑动条目的回调一般写在这里。
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==contentView){
				deleteView.layout(deleteView.getLeft()+dx, 0, deleteView.getRight()+dx, deleteView.getBottom());
			}else{
				contentView.layout(contentView.getLeft()+dx, 0, contentView.getRight()+dx, contentView.getBottom());
			}
			if(contentView.getLeft()==0&&swipeStatus!=SwipeStatus.Close){
				swipeStatus = SwipeStatus.Close;
				if(swipeStatusChangedListener!=null){
					swipeStatusChangedListener.onClose(SwipeView.this);
				}
			}else if(contentView.getLeft()==-deleteViewWidth&&swipeStatus!=SwipeStatus.Open){
				swipeStatus = SwipeStatus.Open;
				if(swipeStatusChangedListener!=null){
					swipeStatusChangedListener.onOpen(SwipeView.this);
				}
			}else if(swipeStatus!=SwipeStatus.Swiping){
				swipeStatus = SwipeStatus.Swiping;
				if(swipeStatusChangedListener!=null){
					swipeStatusChangedListener.onSwipe(SwipeView.this);
				}
			}
		}
		//滑动释放后的一些动画效果一般写在这里。
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteViewWidth/2.0f){//如果左边的view拖拽出的距离大于右边view的一半，那么释放后自动打开。
				if(!hasOpen){
					open(true);
				}else{
					close(true);
				}
			}else{//否则自动关闭
				close(true);
			}
		}
	};

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		//规定子类的最初位置。
		contentView.layout(0, 0, contentViewWidth, deleteViewHeight);
		deleteView.layout(contentViewWidth, 0, contentViewWidth+deleteViewWidth,deleteViewHeight);
	}
	/**
	 * 关闭拉出的条目
	 * @param isSmooth 是否平滑移动
	 */
	public void close(boolean isSmooth) {
		if(isSmooth){
			if(dragHelper.smoothSlideViewTo(contentView, 0, 0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			contentView.layout(0, 0, contentViewWidth, deleteViewHeight);
			deleteView.layout(contentViewWidth, 0, contentViewWidth+deleteViewWidth,deleteViewHeight);
			swipeStatus = SwipeStatus.Close;
			if(swipeStatusChangedListener!=null){
				swipeStatusChangedListener.onClose(this);
			}
		}
		hasOpen = false;
	}
	/**
	 * 打开拉出的条目
	 * @param isSmooth 是否平滑移动
	 */
	public void open(boolean isSmooth) {
		if(isSmooth){
			if(dragHelper.smoothSlideViewTo(contentView, -deleteViewWidth, 0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			contentView.layout(-deleteViewWidth, 0, -deleteViewWidth+contentViewWidth, deleteViewHeight);
			deleteView.layout(contentViewWidth-deleteViewWidth, 0, contentViewWidth, deleteViewHeight);
		}
		hasOpen = true;
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(dragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	//可以通过这个方法获取子view的尺寸。
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		contentViewWidth = contentView.getMeasuredWidth();
		deleteViewWidth = deleteView.getMeasuredWidth();
		deleteViewHeight = deleteView.getMeasuredHeight();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev);
	}
	
	private int lastX,lastY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = lastX - x;
			int deltaY = lastY - y;
			//屏蔽滑动条目时整个listview也跟着移动的事件。
			if(Math.abs(deltaX)>Math.abs(deltaY)){
				requestDisallowInterceptTouchEvent(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		lastX = x;
		lastY = y;
		dragHelper.processTouchEvent(event);
		return true;
	}
	//这个方法通过xml文件加载完布局，只知道有几个子类，但是没有测量子类的尺寸，一般适用于子类实例化。
	@Override
	protected void onFinishInflate() {
		deleteView = getChildAt(0);
		contentView = getChildAt(1);
	}
}
