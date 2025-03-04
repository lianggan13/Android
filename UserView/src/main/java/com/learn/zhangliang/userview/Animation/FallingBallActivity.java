package com.learn.zhangliang.userview.Animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/12/7.
 */
public class FallingBallActivity extends Activity {
    private FallingBallImageView ball_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.falling_ball_activity);

        ball_img = (FallingBallImageView) findViewById(R.id.ball_img);

        findViewById(R.id.start_anim).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofObject(ball_img, "fallingPos", new FallingBallEvaluator(), new Point(0, 0), new Point(500, 500));
//                ObjectAnimator animator = ObjectAnimator.ofObject(ball_img, "fallingPos", new FallingBallEvaluator(), new Point(500, 500));
                animator.setDuration(2000);
                animator.start();
            }
        });
    }
}
