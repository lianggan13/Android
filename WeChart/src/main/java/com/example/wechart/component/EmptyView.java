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

public class EmptyView extends RelativeLayout {

    private TextView mTvEmptyMessage;
    private ImageView mIvEmptyIcon;

    public EmptyView(Context context) {
        super(context);
        initView();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.empty_view, this, true);
        mTvEmptyMessage = findViewById(R.id.empty_message);
        mIvEmptyIcon = findViewById(R.id.empty_icon);
    }
}