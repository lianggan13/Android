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
 * Created by qijian on 16/12/25.
 */
public class AliPayView extends View {
    private Path mCirclePath, mDstPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Float mCurAnimValue;
    private int mCentX = 100;
    private int mCentY = 100;
    private int mRadius = 50;

    public AliPayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.BLACK);

        mDstPath = new Path();
        mCirclePath = new Path();

        mCirclePath.addCircle(mCentX, mCentY, mRadius, Path.Direction.CW);

        mCirclePath.moveTo(mCentX - mRadius / 2, mCentY);
        mCirclePath.lineTo(mCentX, mCentY + mRadius / 2);
        mCirclePath.lineTo(mCentX + mRadius / 2, mCentY - mRadius / 3);

        mPathMeasure = new PathMeasure(mCirclePath, false);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 2); // fFloat(0, 2): 有两条路径，在 0～1 之间时画第一条路径，在 1～2 之间时画第二条路径
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurAnimValue = (Float) animation.getAnimatedValue(); // 根据动画进度计算出当前路径的长度
                invalidate();
            }
        });
        animator.setDuration(4000);
        animator.start();
    }

    boolean mNext = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if (mCurAnimValue < 1) {
            // 当 mCurAnimValue < 1 时，画外圈的圆形
            float stop = mPathMeasure.getLength() * mCurAnimValue;
            mPathMeasure.getSegment(0, stop, mDstPath, true);
        } else {
            if (!mNext) {
                //if (mCurAnimValue == 1) { // 张亮注：mCurAnimValue 不刚好 等于 1
                // 当 mCurAnimValue == 1 时，说明圆已经画完，
                // 先利用 mPathMeasure.getSegment() 函数将圆画完，
                // 然后调用 mPathMeasure.nextContour() 函数将路径转到对钩路径上
                mNext = true;
                mPathMeasure.getSegment(0, mPathMeasure.getLength(), mDstPath, true);
                mPathMeasure.nextContour(); // nextContour: 将 Path 由 圆形 转到 对钩路径继续
            }
            //当 mCurAnimValue>1 时，说明已经在对钩路径上了，
            // 需要注意的是，此时 mCurAnimValue 的值在 1～2 之间，所以在求终点时，需要减去 1。
            float stop = mPathMeasure.getLength() * (mCurAnimValue - 1);
            mPathMeasure.getSegment(0, stop, mDstPath, true);
        }
        canvas.drawPath(mDstPath, mPaint);
    }
}
