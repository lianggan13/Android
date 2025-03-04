package com.learn.zhangliang.userview.Animation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by qijian on 16/12/23.
 */
public class GetSegmentView extends View {

    private Path mCirclePath, mDstPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Float mCurAnimValue;

    public GetSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.BLACK);

        mDstPath = new Path();
        mCirclePath = new Path();
        mCirclePath.addCircle(100, 100, 50, Path.Direction.CW);

        mPathMeasure = new PathMeasure(mCirclePath, true);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurAnimValue = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(2000);
        animator.start();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float length = mPathMeasure.getLength();
        float stop = length * mCurAnimValue;
        mDstPath.reset();
        canvas.drawColor(Color.WHITE);

        float start = 0;
        // 动态更改起始点 start: 当进度为 0～0.5 时，路径的起始点都是 0；而进度为 0.5～1 时，路径的起始点逐渐靠近终点；当进度为 1 时，两点重合。
        start = (float) (stop - ((0.5 - Math.abs(mCurAnimValue - 0.5)) * length));
//        if(start >= 0.5){
//            start = 2 * mCurAnimValue - 1;
//        }

        mPathMeasure.getSegment(start, stop, mDstPath, true);

        canvas.drawPath(mDstPath, mPaint);
    }
}
