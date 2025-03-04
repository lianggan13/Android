package com.learn.zhangliang.userview.CanvasOperation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/11/4.
 */

public class ClipRgnView extends View {
    private Bitmap mBitmap;
    private int clipWidth = 0;
    private int width;
    private int heigth;
    private static final int CLIP_HEIGHT = 30;
    private Region mRgn;

    public ClipRgnView(Context context) {
        super(context);
        init();
    }

    public ClipRgnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipRgnView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scenery);
        width = mBitmap.getWidth();
        heigth = mBitmap.getHeight();
        mRgn = new Region();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mRgn.setEmpty();
        int i = 0;
        while (i * CLIP_HEIGHT <= heigth) {//计算clip的区域
            if (i % 2 == 0) {
                mRgn.union(new Rect(0, i * CLIP_HEIGHT, clipWidth, (i + 1) * CLIP_HEIGHT));
            } else {
                mRgn.union(new Rect(width - clipWidth, i * CLIP_HEIGHT, width, (i + 1)
                        * CLIP_HEIGHT));
            }
            i++;
        }

        canvas.clipRect(mRgn.getBounds());//.clipRegion(mRgn);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
        if (clipWidth > width) {
            return;
        }
        clipWidth += 5;

        invalidate();
    }
}
