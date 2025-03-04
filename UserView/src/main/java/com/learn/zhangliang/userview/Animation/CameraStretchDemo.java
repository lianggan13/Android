package com.learn.zhangliang.userview.Animation;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/11/16.
 */
public class CameraStretchDemo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_stretch_activity);

        ImageView imageView = (ImageView) findViewById(R.id.img);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setInterpolator(new BounceInterpolator());
        scaleAnim.setDuration(6000);
        imageView.startAnimation(scaleAnim);
    }
}
