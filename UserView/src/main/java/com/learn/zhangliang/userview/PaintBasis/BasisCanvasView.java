package com.learn.zhangliang.userview.PaintBasis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by qijian on 16/10/12.
 */
public class BasisCanvasView extends View {

    public BasisCanvasView(Context context) {
        super(context);
    }

    public BasisCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasisCanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 1.1.3.1 画布背景设置
         */
//        canvas.drawRGB(255, 0, 255);


        /**
         * 1.1.3.2 画直线
         */
//        Paint paint=new Paint();
//        paint.setColor(Color.RED);  //设置画笔颜色
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setStrokeWidth(5);//设置画笔宽度
//
//        canvas.drawLine(100, 100, 200, 200, paint);


        /**
         * 多条直线
         */
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(5);
//
//        float[] pts = {10, 10, 100, 100, 200, 200, 400, 400};
//        canvas.drawLines(pts, paint);
////        canvas.drawLines(pts, 2, 4, paint);


        /**
         * 画点
         */
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(15);
//        canvas.drawPoint(100, 100, paint);


        /**
         * 多点
         */
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(25);
//
//        float[] pts = {10, 10, 100, 100, 200, 200, 400, 400};
//        canvas.drawPoints(pts, 2, 4, paint);


        /**
         * 矩形
         */
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(15);
//
//        //直接构造
//        canvas.drawRect(10, 10, 100, 100, paint);
//
//        //使用RectF构造
//        paint.setStyle(Paint.Style.FILL);
//        RectF rect = new RectF(210f, 10f, 300f, 100f);
//        canvas.drawRect(rect, paint);


        /**
         * 圆角矩形
         */
//        Paint paint=new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(15);
//
//        RectF rect = new RectF(100, 10, 300, 100);
//        canvas.drawRoundRect(rect, 20, 10, paint);


        /**
         * 圆
         */
//        Paint paint=new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(15);
//
//        canvas.drawCircle(150, 150, 100, paint);

        /**
         * 画椭圆
         */
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);
//
//        RectF rect = new RectF(100, 10, 300, 100);
//        canvas.drawRect(rect, paint);
//
//        paint.setColor(Color.GREEN);//更改画笔颜色
//        canvas.drawOval(rect, paint);//同一个矩形画椭圆


        /**
         * 画弧
         */
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        RectF rect1 = new RectF(10, 10, 100, 100);
        canvas.drawArc(rect1, 0, 90, true, paint);

        RectF rect2 = new RectF(110, 10, 200, 100);
        canvas.drawArc(rect2, 0, 90, false, paint);


    }
}
