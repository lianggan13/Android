package com.learn.zhangliang.userview.Animation;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/12/6.
 */
public class CharacterEvaluatorActivity extends Activity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.evaluator_activity);

        tv = (TextView) findViewById(R.id.tv);

        findViewById(R.id.start_anim).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ValueAnimator animator = ValueAnimator.ofObject(new CharEvaluator(), new Character('A'), new Character('Z'));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        char text = (Character) animation.getAnimatedValue();
                        tv.setText(String.valueOf(text));
                    }
                });
                animator.setDuration(10000);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }
        });
    }
}
