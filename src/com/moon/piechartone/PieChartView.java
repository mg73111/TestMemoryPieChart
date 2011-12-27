package com.moon.piechartone;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class PieChartView extends View {

	private List<PieChartItem> mDataArray;
	
	private int mState;
	private int mMaxConnection;
	private int mBgColor;
	private int mWidth;
	private int mHeight;
	private int mGapLeft;
	private int mGapRight;
	private int mGapTop;
	private int mGapBottom;
	private float mStartAngle;
	private boolean mUseCenter;

	private static final int IS_READY_TO_DRAW = 1;
	private static final int IS_DRAW = 2;
	private static final float START_ANGLE = 30;
	
	public PieChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mState != IS_READY_TO_DRAW) return;
		
		mStartAngle = START_ANGLE;
		mUseCenter = true;
		
		canvas.drawColor(mBgColor);
		
		Paint mBgPaints = new Paint();
    	mBgPaints.setAntiAlias(true);
    	mBgPaints.setStyle(Paint.Style.FILL);
    	mBgPaints.setColor(Color.BLACK);
    	mBgPaints.setStrokeWidth(0.0f);
    	
    	// 파이차트 화면에서의 위치
    	RectF mOvals = new RectF(mGapLeft, mGapTop, mWidth - mGapRight, mHeight - mGapBottom);
    	
    	for(PieChartItem item : mDataArray){
    		
    		mBgPaints.setColor(item.Color);
    		float sweepAngle = (float) 360 * ((float) item.Count / (float) mMaxConnection);
    		canvas.drawArc(mOvals, mStartAngle, sweepAngle, mUseCenter, mBgPaints);
    		mStartAngle = mStartAngle + sweepAngle;
    	}
    	
    	mState = IS_DRAW;
	}
	
    public void setGeometry(int width, int height, int gapLeft, int gapRight, 
    		int gapTop, int gapBottom) {
    	mWidth     = width;
   	 	mHeight    = height;
   	 	mGapLeft   = gapLeft;
   	 	mGapRight  = gapRight;
   	 	mGapTop    = gapTop;
   	 	mGapBottom = gapBottom;
    }
	
    /**
     * 차트 배경색 설정
     * @param bgColor
     */
    public void setSkinParams(int bgColor) {
   	 	mBgColor   = bgColor;
    }
	
    public void setState(int State) {
    	mState = State;
    }
    
    public void setData(List<PieChartItem> data, int MaxSize) {
    	mDataArray = data;
    	mMaxConnection = MaxSize;
    	mState = IS_READY_TO_DRAW;
    }
}
