package com.learn.zhangliang.userview.Animation;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by qijian on 16/12/7.
 */
public class FallingBallEvaluator implements TypeEvaluator<Point> {
    private Point point = new Point();

    public Point evaluate(float fraction, Point startValue, Point endValue) {
        point.x = (int) (startValue.x + fraction * (endValue.x - startValue.x));

        if (fraction * 2 <= 1) {
            point.y = (int) (startValue.y + fraction * 2 * (endValue.y - startValue.y));
        } else {
            point.y = endValue.y;
        }
        return point;
    }
}
