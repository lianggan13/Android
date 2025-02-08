package com.example.wechart.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getName();
    protected View mView;
    public Context mContext;

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        onCreateFragmentView(inflater, container, savedInstanceState);
        return mView;
    }

    protected void onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        initListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //基类中添加通用方法
    public void showToast(String message) {
        if(mContext != null && message != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }
}
