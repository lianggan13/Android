package com.learn.zhangliang.userview.CanvasOperation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/11/6.
 */

public class CustomViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_view_activity);

        /**
         * layout_wight定义
         */
//        LinearLayout rootView = (LinearLayout) findViewById(R.id.root);
//        CustomView customView = new CustomView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.weight = 1.0f;
//        rootView.addView(customView, layoutParams);


        /**
         * RelativeLayout添加布局规则
         */
//        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root);
//        CustomView customView = new CustomView(this);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.text);
//        rootView.addView(customView, layoutParams);


        /**
         * setGravity使用
         */
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 200);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.text);

        Button button = new Button(this);
        button.setGravity(Gravity.TOP);
        button.setText("btn");
        rootView.addView(button, layoutParams);
        rootView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

    }
}
