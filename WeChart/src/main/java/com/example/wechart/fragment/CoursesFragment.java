package com.example.wechart.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wechart.R;
import com.example.wechart.component.HeaderView;
import com.example.wechart.fragment.base.BaseFragment;


public class CoursesFragment extends BaseFragment {

    private HeaderView mHeaderView;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_courses;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        mHeaderView = mView.findViewById(R.id.header_view);
        mHeaderView.setTitle("我的课程");
    }

    @Override
    protected void initListener() {

    }
}