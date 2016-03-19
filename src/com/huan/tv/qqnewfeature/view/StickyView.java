package com.huan.tv.qqnewfeature.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.huan.tv.qqnewfeature.util.GeometryUtil;
import com.huan.tv.qqnewfeature.util.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
/**
 * 消息列表上标示消息条数的粘性控件。
 * @author MrTaoge
 *
 */
public class StickyView extends View {
	private Paint paint;
	private Path path;
	/**拖拽圆可以移动的最大距离，超过这个距离后就不再画连接线，而只有一个拖拽圆，形成了断裂的效果*/
	private float maxDistance = 80f;
	private PointF[] dragPoints;
	private PointF[] stickyPoints;
	private PointF circleCenterDrag = new PointF(150f,150f);
	private PointF circleCenterSticky = new PointF(100f,100f);
	private float stickyCircleRadius = 12f;
	private float dragCircleRadius = 16f;
	/**两个圆是否还是连接状态*/
	private boolean isConnected = true;
	/**连接着的情况下，拖拽圆是否超出最大可拖拽范围*/
	private boolean isOutOfRange = false;
	private float distance = GeometryUtil.getDistanceBetween2Points(circleCenterSticky, circleCenterDrag);
	private DragMotionEventEndListener dragMotionEndListener;
	private Paint textPaint;
	
	public StickyView(Context context) {
		this(context,null);
	}

	public StickyView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public StickyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);//去锯齿。
		paint.setColor(Color.RED);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(15);
		path = new Path();
	}
	
	public interface DragMotionEventEndListener{
		void onDisapper();
		void onReset(boolean isOutOfRange);
	}
	public void setOnDragMotionEventListener(DragMotionEventEndListener dragMotionEndListener){
		this.dragMotionEndListener = dragMotionEndListener;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(0, -Utils.getStatusBarHeight(this));
		//两个圆圆心的横坐标偏移量
		float xOff = circleCenterSticky.x-circleCenterDrag.x;
		//两个圆的圆心的纵坐标偏移量
		float yOff = circleCenterSticky.y-circleCenterDrag.y;
		//由两个圆的圆心的距离来决定固定圆的大小(半径)。
//		float distance = GeometryUtil.getDistanceBetween2Points(circleCenterSticky, circleCenterDrag);
		float tempStickyCircleRadius = getRadiusByDistance(distance);
		//获取直线的斜率，用于计算直线与圆的焦点。
		Double lineK = null;
		if(xOff!=0){
			lineK = (double) (yOff/xOff);
		}
		//计算曲线与拖拽圆的切点(两条线两个点)
		dragPoints = GeometryUtil.getIntersectionPoints(circleCenterDrag, dragCircleRadius, lineK);
		//计算固定圆与曲线之间的切点。
		stickyPoints = GeometryUtil.getIntersectionPoints(circleCenterSticky, tempStickyCircleRadius, lineK);
		//计算画曲线需要的控制点
		PointF controlPoint = GeometryUtil.getPointByPercent(circleCenterDrag, circleCenterSticky, 0.618f);
//		path.reset();这个方法对于"填充型"的线型不管用，但是这里可以用这个方法。
		path.rewind();//再次画线之前应该清除之前的所有线的数据，以免造成重复使用。可以每次执行画的动作时都在这里创建一个新的实例，但这样一般不提倡，会有警告。如果设置成成员变量，那么就要像里一样每次使用前清除数据。
		//开始从一个切点画曲线以及固定圆。
		if(isConnected){
			if(!isOutOfRange){
				path.moveTo(dragPoints[0].x, dragPoints[0].y);
				path.quadTo(controlPoint.x, controlPoint.y, stickyPoints[0].x, stickyPoints[0].y);
				path.lineTo(stickyPoints[1].x,  stickyPoints[1].y);
				path.quadTo(controlPoint.x, controlPoint.y, dragPoints[1].x, dragPoints[1].y);
				path.close();//将当前的图形闭合,这里相当于path.lineTo(dragPointA.x, dragPointA.y);。
				//将最终的线画出来。
				canvas.drawPath(path, paint);//path封装了一些列有关线的画法，包括直线，二次曲线，三次曲线等
				//画固定圆
				canvas.drawCircle(circleCenterSticky.x,circleCenterSticky.y, tempStickyCircleRadius, paint);//画布执行画的动作和所画图形的形状及大小，画笔来规定图形的颜色等具体的填充效果。
			}
				//画拖拽圆
				canvas.drawCircle(circleCenterDrag.x,circleCenterDrag.y, dragCircleRadius, paint);
				canvas.drawText("20", circleCenterDrag.x, circleCenterDrag.y+dragCircleRadius/2.0f, textPaint);
		}
		canvas.restore();
	}
	
	private float getRadiusByDistance(float distance) {
		distance = Math.min(distance, maxDistance);
		float percent = distance/maxDistance;
		return evaluate(percent,stickyCircleRadius,stickyCircleRadius*0.4f);
	}

	private float evaluate(float percent, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat+percent*(endValue.floatValue()-startFloat);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (MotionEventCompat.getActionMasked(event)) {//为了兼容低版本，使用这个帮助类。
		case MotionEvent.ACTION_DOWN:
			isOutOfRange = false;//防止松手拖拽圆消失的情况下，触摸屏幕时再次出现两个圆。
			isConnected = true;
			float downX = event.getRawX();
			float downY = event.getRawY();
			updateDragPosition(downX,downY);
			Log.i("TAG", "按下");
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			updateDragPosition(moveX,moveY);
			distance = GeometryUtil.getDistanceBetween2Points(circleCenterSticky, circleCenterDrag);
			if(distance>maxDistance){
				isOutOfRange = true;
			}
			invalidate();
			Log.i("TAG", "distance=="+distance);
			break;
		case MotionEvent.ACTION_UP:
			//松手后有两种大的情况：
			if(isOutOfRange){//1.两个圆已经断开连接，此时又分两种小的情况：
				if(distance<maxDistance){//<1>.超出拖拽范围后没有立刻松手而是拉回到拖拽范围内又松的手，此时弹回去。
					isConnected = true;
					updateDragPosition(circleCenterSticky.x, circleCenterSticky.y);//和固定圆重合。
					if(dragMotionEndListener!=null){
						dragMotionEndListener.onReset(isOutOfRange);
					}
				}else{//<2>.超出拖拽范围后松手的话销毁拖拽圆和固定圆。也就是停止任何画图的动作。
					isConnected = false;
					invalidate();
					if(dragMotionEndListener!=null){
						dragMotionEndListener.onDisapper();
					}
				}
			}else{ //2.两个圆还相连着就松手，此时拖拽圆弹回去。
				isConnected = true;
				ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f);
				valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
					 PointF startPoint = new PointF(circleCenterDrag.x,circleCenterDrag.y);
					 PointF endPoint = new PointF(circleCenterSticky.x,circleCenterSticky.y);
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						float fraction = valueAnimator.getAnimatedFraction();
						PointF springbackPoint = GeometryUtil.getPointByPercent(startPoint, endPoint, fraction);
						updateDragPosition(springbackPoint.x, springbackPoint.y);
					}
				});
				valueAnimator.setInterpolator(new OvershootInterpolator(5.0f));
				valueAnimator.setDuration(500);
				valueAnimator.start();
				valueAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						if(dragMotionEndListener!=null){
							dragMotionEndListener.onReset(isOutOfRange);
						}
					}
				});
				invalidate();
			}
			break;

		default:
			break;
		}
		return true;
	}
	/**
	 * 不断更新拖拽点的位置。
	 * @param x
	 * @param y
	 */
	private void updateDragPosition(float x, float y) {
		circleCenterDrag.set(x, y);
		invalidate();
	}
}
