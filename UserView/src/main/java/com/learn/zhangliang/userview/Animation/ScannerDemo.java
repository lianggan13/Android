package com.learn.zhangliang.userview.Animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/11/16.
 */
public class ScannerDemo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        //边放大边降低透明度
        final Animation animation1 = AnimationUtils.loadAnimation(ScannerDemo.this, R.anim.scale_alpha_anim);
        final Animation animation2 = AnimationUtils.loadAnimation(ScannerDemo.this, R.anim.scale_alpha_anim);
        final Animation animation3 = AnimationUtils.loadAnimation(ScannerDemo.this, R.anim.scale_alpha_anim);
        final Animation animation4 = AnimationUtils.loadAnimation(ScannerDemo.this, R.anim.scale_alpha_anim);

        final ImageView circle1 = (ImageView) findViewById(R.id.circle1);
        final ImageView circle2 = (ImageView) findViewById(R.id.circle2);
        final ImageView circle3 = (ImageView) findViewById(R.id.circle3);
        final ImageView circle4 = (ImageView) findViewById(R.id.circle4);

        findViewById(R.id.start_can).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                circle1.startAnimation(animation1);

                animation2.setStartOffset(600);
                circle2.startAnimation(animation2);

                animation3.setStartOffset(1200);
                circle3.startAnimation(animation3);

                animation4.setStartOffset(1800);
                circle4.startAnimation(animation4);
            }
        });
    }
}
