package com.example.zhangliang.videonews.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class CardViewAdapter extends FragmentStateAdapter {

    private String[] mTitles;
    private ArrayList<Fragment> mFragments;

    public CardViewAdapter(FragmentActivity activity, String[] titles, ArrayList<Fragment> fragments) {
        super(activity);
        this.mTitles = titles;
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
