package com.example.wechart.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.wechart.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HeaderView extends RelativeLayout {

    private TextView mTitleView;
    private Button mButtonLeft;
    private ImageButton mButtonRight;

    public HeaderView(Context context) {
        super(context);
        initView();
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.header_view, this, true);
        mTitleView = findViewById(R.id.title);
        mButtonLeft = findViewById(R.id.button_left);
        mButtonRight = findViewById(R.id.button_right);
    }

    public void setTitle(String title) {
        if (mTitleView != null && !TextUtils.isEmpty(title)) {
            mTitleView.setText(title);
        }
    }

    public void setLeftText(String text) {
        mButtonLeft.setText(text);
    }

    public void setRightSrc(int id) {
        mButtonRight.setImageResource(id);
    }

    public void setLeftClickListener(OnClickListener listener) {
        mButtonLeft.setOnClickListener(listener);
    }

    public void setRightClickListener(OnClickListener listener) {
        mButtonRight.setOnClickListener(listener);
    }

    public void setLeftVisibility(int visiblity) {
        mButtonLeft.setVisibility(visiblity);
    }

    public void setRightVisibility(int visibility) {
        mButtonRight.setVisibility(visibility);
    }

}
