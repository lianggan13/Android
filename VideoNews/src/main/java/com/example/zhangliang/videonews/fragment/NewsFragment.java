package com.example.zhangliang.videonews.fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.zhangliang.videonews.R;
import com.example.zhangliang.videonews.adapter.NewsAdapter;
import com.example.zhangliang.videonews.api.Api;
import com.example.zhangliang.videonews.api.ApiConfig;
import com.example.zhangliang.videonews.api.HttpCallback;
import com.example.zhangliang.videonews.database.MysqlSeed;
import com.example.zhangliang.videonews.entity.NewsEntity;
import com.example.zhangliang.videonews.entity.NewsListResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends BaseFragment {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;

    private NewsAdapter newsAdapter;
    private List<NewsEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int pageNum = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    newsAdapter.setDatas(datas);
                    newsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private final ViewPager2.PageTransformer mAnimator = new ViewPager2.PageTransformer() {
        @Override
        public void transformPage(@NonNull View page, float position) {
            float absPos = Math.abs(position);
            page.setTranslationX(0);
            page.setAlpha(1);

//            page.setRotation( position * 360);

//            page.setTranslationY(absPos * 500f);
//            page.setTranslationX(absPos * 350f);

//            float scale = absPos > 1 ? 0f : 1 - absPos;
//            page.setScaleX(scale);
//            page.setScaleY(scale);
        }
    };

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView() {
        viewPager2 = mRootView.findViewById(R.id.view_pager);
        tabLayout = mRootView.findViewById(R.id.tabs);

        viewPager2.setPageTransformer(mAnimator);
        viewPager2.requestTransform();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int targetPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        recyclerView = mRootView.findViewById(R.id.recyclerView);
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);
    }

    @Override
    protected void initData() {
//        getVideoCategoryListByLocal();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        newsAdapter = new NewsAdapter(getActivity());
        recyclerView.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Serializable obj) {
                NewsEntity newsEntity = (NewsEntity) obj;
                showToast(newsEntity.getHeaderUrl());

                return;
                //  String url = "http://192.168.31.32:8089/newsDetail?title=" + newsEntity.getAuthorName();
                //  String url = "http://10.60.0.66:82";
                //  String url = "http://10.60.0.179:8080/api/users";
                //  Bundle bundle = new Bundle();
                //  bundle.putString("url", url);
                //  navigateToWithBundle(WebActivity.class, bundle);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNum = 1;
//                getNewsList(true);
                getNewsListByLocal(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageNum++;
//                getNewsList(false);
                getNewsListByLocal(false);
            }
        });
//        getNewsList(true);
        getNewsListByLocal(true);
    }

    private void getNewsListByLocal(final boolean isRefresh) {
        try {
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }

            var videolist = MysqlSeed.loadLocalNews();
            if (videolist != null && (long) videolist.size() > 0) {
                List<NewsEntity> list = MysqlSeed.paginateNews(videolist, pageNum, ApiConfig.PAGE_SIZE);
                if (list != null && list.size() > 0) {
                    if (isRefresh) {
                        datas = list;
                    } else {
                        datas.addAll(list);
                    }
                    mHandler.sendEmptyMessage(0);
                } else {
                    if (isRefresh) {
                        showToast("暂时无数据");
                    } else {
                        showToast("没有更多数据");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("NewsFragment", e.getMessage(), e);
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }
        }
    }

    private void getNewsList(final boolean isRefresh) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", pageNum);
        params.put("limit", ApiConfig.PAGE_SIZE);
        Api.config(ApiConfig.NEWS_LIST, params).getRequest(getActivity(), new HttpCallback() {
            @Override
            public void onSuccess(final String res) {
                if (isRefresh) {
                    refreshLayout.finishRefresh(true);
                } else {
                    refreshLayout.finishLoadMore(true);
                }
                NewsListResponse response = new Gson().fromJson(res, NewsListResponse.class);
                if (response != null && response.getCode() == 0) {
                    List<NewsEntity> list = response.getPage().getList();
                    if (list != null && list.size() > 0) {
                        if (isRefresh) {
                            datas = list;
                        } else {
                            datas.addAll(list);
                        }
                        mHandler.sendEmptyMessage(0);
                    } else {
                        if (isRefresh) {
                            showToastSync("暂时无数据");
                        } else {
                            showToastSync("没有更多数据");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isRefresh) {
                    refreshLayout.finishRefresh(true);
                } else {
                    refreshLayout.finishLoadMore(true);
                }
            }
        });
    }

}