package com.example.wechart.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * TODO: document your custom view class.
 */
public class MyView1 extends View {

    Paint paint = new Paint();

    public MyView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //设置画笔的颜色
//        paint.setColor(getResources().getColor(R.color.red));
//        paint.setColor(ContextCompat.getColor(context, R.color.light_blue_600));

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        //绘制线
        canvas.drawLine(0, 0, 200, 200, paint);

        //绘制矩形
        canvas.drawRect(100, 0, 200, 100, paint);
        //绘制圆角矩形
        canvas.drawRoundRect(100, 150, 200, 250, 10, 10, paint);

        //绘制圆
        canvas.drawCircle(120, 400, 100, paint);

        //绘制椭圆
        canvas.drawOval(0, 0, 200, 100, paint);
        canvas.drawOval(0, 0, 200, 200, paint);

        //绘制圆弧
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(0, 0, 200, 200, 0, 90, false, paint);

        //绘制图片
//        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sweet);
//        Bitmap bitmap = Bitmap.createScaledBitmap(srcBitmap, 200, 200, true);
//        canvas.drawBitmap(bitmap, 0, 0, paint);

        //绘制文字
        canvas.drawText("比特猫编程", 100, 100, paint);
    }
}