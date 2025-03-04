package com.learn.zhangliang.userview.Draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by qijian on 17/1/14.
 */
public class BezierGestureTrackView extends View {
    private Path mPath = new Path();
    private Paint mPaint;
    private float mPreX, mPreY;

    public BezierGestureTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 把各条线段的中间点作为起始点和终点，把终点前一个手指位置作为控制点
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mPath.moveTo(event.getX(), event.getY());
                // 首次触摸的点 (mPreX,mPreY) 作为路径的起始点和后续曲线绘制的控制点
                mPreX = event.getX();
                mPreY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                // 终点: (endX,endY) 这条线段的中间位置，
                float endX = (mPreX + event.getX()) / 2;
                float endY = (mPreY + event.getY()) / 2;
                mPath.quadTo(mPreX, mPreY, endX, endY);

                // 控制点: 上一个手指位置 (mPreX,mPreY) 起始点就是上一条线段的中间点
                mPreX = event.getX();
                mPreY = event.getY();
                invalidate();
            }
            break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(mPath, mPaint);
    }
}
