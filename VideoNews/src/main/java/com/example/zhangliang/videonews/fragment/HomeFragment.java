package com.example.zhangliang.videonews.fragment;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.zhangliang.videonews.R;
import com.example.zhangliang.videonews.adapter.HomeAdapter;
import com.example.zhangliang.videonews.api.Api;
import com.example.zhangliang.videonews.api.ApiConfig;
import com.example.zhangliang.videonews.api.HttpCallback;
import com.example.zhangliang.videonews.entity.CategoryEntity;
import com.example.zhangliang.videonews.entity.VideoCategoryResponse;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        viewPager = mRootView.findViewById(R.id.fixedViewPager);
        slidingTabLayout = mRootView.findViewById(R.id.slidingTabLayout);
    }

    @Override
    protected void initData() {
//        getVideoCategoryList();
        getVideoCategoryListByLocal();
    }

    private void getVideoCategoryList() {
        HashMap<String, Object> params = new HashMap<>();
        Api.config(ApiConfig.VIDEO_CATEGORY_LIST, params).getRequest(getActivity(), new HttpCallback() {
            @Override
            public void onSuccess(final String res) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoCategoryResponse response = new Gson().fromJson(res, VideoCategoryResponse.class);
                        if (response != null && response.getCode() == 0) {
                            List<CategoryEntity> list = response.getPage().getList();
                            if (list != null && list.size() > 0) {
                                mTitles = new String[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    mTitles[i] = list.get(i).getCategoryName();
                                    mFragments.add(VideoFragment.newInstance(list.get(i).getCategoryId()));
                                }
                                viewPager.setOffscreenPageLimit(mFragments.size());
                                viewPager.setAdapter(new HomeAdapter(getFragmentManager(), mTitles, mFragments));
                                slidingTabLayout.setViewPager(viewPager);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void getVideoCategoryListByLocal() {
        List<CategoryEntity> list = new ArrayList<>();

        list.add(createCategory(1, "游戏"));
        list.add(createCategory(2, "音乐"));
        list.add(createCategory(3, "美食"));
        list.add(createCategory(4, "农人"));
        list.add(createCategory(5, "vlog"));
        list.add(createCategory(6, "搞笑"));
        list.add(createCategory(7, "宠物"));
        list.add(createCategory(8, "军事"));

        if (list != null && list.size() > 0) {
            mTitles = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                mTitles[i] = list.get(i).getCategoryName();
                mFragments.add(VideoFragment.newInstance(list.get(i).getCategoryId()));
//                mFragments.add(Video2Fragment.newInstance(list.get(i).getCategoryId()));
            }
            viewPager.setOffscreenPageLimit(mFragments.size());
            viewPager.setAdapter(new HomeAdapter(getActivity().getSupportFragmentManager(), mTitles, mFragments));
//            viewPager.setAdapter(new HomeAdapter2(getActivity(), mTitles, mFragments));
            slidingTabLayout.setViewPager(viewPager);
        }
    }

    private CategoryEntity createCategory(int id, String name) {
        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(id);
        category.setCategoryName(name);
        return category;
    }
}