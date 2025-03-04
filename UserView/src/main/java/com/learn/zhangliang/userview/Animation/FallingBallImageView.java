package com.learn.zhangliang.userview.Animation;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by qijian on 16/12/7.
 */
public class FallingBallImageView extends ImageView {
    public FallingBallImageView(Context context) {
        super(context);
    }

    public FallingBallImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FallingBallImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFallingPos(Point pos) {
        layout(pos.x, pos.y, pos.x + getWidth(), pos.y + getHeight());
    }

}
