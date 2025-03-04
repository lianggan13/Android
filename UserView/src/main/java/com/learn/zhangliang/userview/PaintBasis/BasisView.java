package com.learn.zhangliang.userview.PaintBasis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by qijian on 16/10/11.
 */
public class BasisView extends View {
    public BasisView(Context context) {
        super(context);
    }

    public BasisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 1.1.1 概述
         */
//    //设置画笔基本属性
//    Paint paint=new Paint();
//    paint.setColor(Color.RED);  //设置画笔颜色
//    paint.setStyle(Paint.Style.STROKE);//设置填充样式
//    paint.setStrokeWidth(50);//设置画笔宽度
//
//    //画圆
//    canvas.drawCircle(190, 200, 150, paint);

        /**
         * 1.1.2.1 setColor
         */
        Paint paint = new Paint();
        paint.setColor(0xFFFF0000);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(50);
        canvas.drawCircle(190, 200, 150, paint);

        paint.setColor(0x7EFFFF00);
        canvas.drawCircle(190, 200, 100, paint);
    }
}
