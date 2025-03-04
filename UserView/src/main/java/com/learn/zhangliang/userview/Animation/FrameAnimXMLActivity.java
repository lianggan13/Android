package com.learn.zhangliang.userview.Animation;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/11/18.
 */
public class FrameAnimXMLActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_anim_xml_activity);

        ImageView image = (ImageView) findViewById(R.id.frame_image);
        final AnimationDrawable anim = (AnimationDrawable) image.getBackground();

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                anim.start();
            }
        });

        findViewById(R.id.stop_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                anim.stop();
            }
        });
    }
}
