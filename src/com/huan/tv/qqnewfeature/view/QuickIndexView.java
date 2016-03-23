package com.huan.tv.qqnewfeature.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 索引的快速导航控件。
 * @author MrTaoge
 *
 */
public class QuickIndexView extends View {

	private Paint paint;
	private Rect bounds;
	private onLetterTouchedChangedListener letterTouchedChangedListener;
	
	private String[] letterArr = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};
	public QuickIndexView(Context context) {
		this(context,null);
	}

	public QuickIndexView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public QuickIndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTypeface(Typeface.DEFAULT_BOLD);//粗体字体。
		paint.setTextSize(16);
		paint.setColor(Color.WHITE);
		bounds = new Rect();
		arrLength = letterArr.length;
	}
	/**触摸到的字母发生变化时的事件监听*/
	public interface onLetterTouchedChangedListener{
		//把变化了的字母暴露出去。
		void onTouchChanged(String letter);
	}
	public void setOnLetterTouchedChangedListener(onLetterTouchedChangedListener changedListener){
		this.letterTouchedChangedListener = changedListener;
	}
	
	/**每个字母所在的格子的宽度*/
	private float gridWidth;
	/**每个字母所在的格子的高度*/
	private float gridHeight;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(gridWidth==0)gridWidth = getMeasuredWidth();
		if(gridHeight==0)gridHeight = getMeasuredHeight()/arrLength;
		
		for (int i = 0; i < arrLength; i++) {
			bounds.setEmpty();
			paint.getTextBounds(letterArr[i], 0, letterArr[i].length(), bounds);
			float startX = gridWidth/2-paint.measureText(letterArr[i])/2;
			float startY = gridHeight/2+bounds.height()/2+gridHeight*i;
			paint.setColor(i==lastIndex?Color.parseColor("#666666"):Color.parseColor("#ffffff"));//被触摸到的当前字母变色。
			canvas.drawText(letterArr[i], startX, startY, paint);
		}
	}
	
	private int lastIndex = -1;
	private int arrLength;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getY();
		switch (MotionEventCompat.getActionMasked(event)) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		int index = (int) (y/gridHeight);
		if(isSameLetter(index))
			break;
		lastIndex = index;
		if(lastIndex>=0&&lastIndex<arrLength){
			if(letterTouchedChangedListener!=null){
				String letter = letterArr[index];
				letterTouchedChangedListener.onTouchChanged(letter);
			}
		}
			break;
		case MotionEvent.ACTION_UP:
			lastIndex = -1;
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}
	/**触摸到的新字母是否和之前的一样*/
	private boolean isSameLetter(int index) {
		return index==lastIndex;
	}

}
