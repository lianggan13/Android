package com.learn.zhangliang.userview.CanvasOperation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by qijian on 16/11/6.
 */

public class CustomView extends View {
    private String TAG = "customView";

    public CustomView(Context context) {
        super(context);
        Log.d(TAG, "context");
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "attrs");
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "defStyle");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        canvas.drawRect(0, 0, 200, 100, paint);
    }
}
