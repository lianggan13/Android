package com.learn.zhangliang.userview.PaintBasis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by qijian on 16/10/15.
 */
public class IntersetView extends View {
    public IntersetView(Context context) {
        super(context);
    }

    public IntersetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntersetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        /**
         * 静态方法判断是否相关
         */
        Rect rect_1 = new Rect(10, 10, 200, 200);
        Rect rect_2 = new Rect(190, 10, 250, 200);
        Rect rect_3 = new Rect(10, 210, 200, 300);
        //分别画出三个矩形
        paint.setColor(Color.RED);
        canvas.drawRect(rect_1, paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rect_2, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rect_3, paint);

        //判断是否相交
        Boolean interset1_2 = Rect.intersects(rect_1, rect_2);
        Boolean interset1_3 = Rect.intersects(rect_1, rect_3);

        Log.d("qijian", "rect_1&rect_2:" + interset1_2 + "  rect_1&rect_3:" + interset1_3);


        /**
         * 成员方法判断是否与某矩形相交
         */
//        Rect rect_1 = new Rect(10, 10, 200, 200);
//        Boolean result_1 = rect_1.intersects(190, 10, 250, 200);
//        printResult(result_1, rect_1);
//
//        Boolean result_2 = rect_1.intersect(210, 10, 250, 200);
//        printResult(result_2, rect_1);
//
//        Boolean result_3 = rect_1.intersect(190, 10, 250, 200);
//        printResult(result_3, rect_1);

        /**
         * 合并两个矩形
         */
//        Rect rect_1 = new Rect(10, 10, 20, 20);
//        Rect rect_2 = new Rect(100, 100, 110, 110);
//
//        //分别画出源矩形rect_1\rect_2
//        paint.setColor(Color.RED);
//        canvas.drawRect(rect_1, paint);
//        paint.setColor(Color.GREEN);
//        canvas.drawRect(rect_2, paint);
//
//        //画出union之后的结果rect_1
//        paint.setColor(Color.YELLOW);
//        rect_1.union(rect_2);
//        canvas.drawRect(rect_1, paint);

        /**
         * 合并点
         */
//        Rect rect_1 = new Rect(10, 10, 20, 20);
//        rect_1.union(100, 100);
//        printResult(rect_1);
//
//        rect_1 = new Rect();
//        rect_1.union(100, 100);
//        printResult(rect_1);

    }

    private void printResult(Boolean result, Rect rect) {
        Log.d("qijian", rect.toShortString() + "  result:" + result);
    }

    private void printResult(Rect rect) {
        Log.d("qijian", rect.toShortString());
    }
}
