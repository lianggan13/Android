package com.example.zhangliang.videonews.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class HomeAdapter2 extends FragmentStateAdapter {

    private String[] mTitles;
    private ArrayList<Fragment> mFragments;

    public HomeAdapter2(@NonNull FragmentActivity fragmentActivity, String[] titles, ArrayList<Fragment> fragments) {
        super(fragmentActivity);
        this.mTitles = titles;
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置创建并返回相应的 Fragment
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
