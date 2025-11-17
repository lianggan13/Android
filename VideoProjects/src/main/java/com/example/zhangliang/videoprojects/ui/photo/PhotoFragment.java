package com.example.zhangliang.videoprojects.ui.photo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.zhangliang.videoprojects.R;
import com.example.zhangliang.videoprojects.adapter.PhotoAdapter;
import com.example.zhangliang.videoprojects.api.ApiConfig;
import com.example.zhangliang.videoprojects.database.MysqlSeed;
import com.example.zhangliang.videoprojects.entity.PhotoEntity;
import com.example.zhangliang.videoprojects.ui.BaseFragment;
import com.example.zhangliang.videoprojects.util.ImageLoader;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends BaseFragment {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;

    private PhotoAdapter photoAdapter;
    private List<PhotoEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int pageNum = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    photoAdapter.setDatas(datas);
                    photoAdapter.notifyDataSetChanged();
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

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_photo;
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        photoAdapter = new PhotoAdapter(getActivity());
        recyclerView.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Serializable obj) {
                if (obj instanceof String) {
                    String imageUrl = (String) obj;
                    showImageDialog(imageUrl);
                    return;
                }

                PhotoEntity newsEntity = (PhotoEntity) obj;
                showToast(newsEntity.getNewsTitle());

                return;
                //  String url = "http://192.168.31.32:8089/newsDetail?title=" + newsEntity.getAuthorName();
                //  Bundle bundle = new Bundle();
                //  bundle.putString("url", url);
                //  navigateToWithBundle(WebActivity.class, bundle);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNum = 1;
                getPhotoListByLocal(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageNum++;
                getPhotoListByLocal(false);
            }
        });
        getPhotoListByLocal(true);
    }

    private void showImageDialog(String imageUrl) {
        try {
            // 使用 PhotoView 支持手势缩放/平移
            com.github.chrisbanes.photoview.PhotoView photoView =
                    new com.github.chrisbanes.photoview.PhotoView(requireContext());
            photoView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            ));
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setBackgroundColor(Color.WHITE);

            // 使用现有的 ImageLoader 加载图片到 PhotoView（与 ImageView 接口兼容）
            ImageLoader loader = new ImageLoader(getActivity());
            loader.load(imageUrl, photoView);

            // 全屏风格的对话框
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(photoView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            }

            dialog.show();
            // 点击关闭对话框
            photoView.setOnClickListener(v -> dialog.dismiss());
        } catch (Exception e) {
            Log.e("PhotoFragment", "showImageDialog failed", e);
            showToast("无法显示图片");
        }
    }


    private void getPhotoListByLocal(final boolean isRefresh) {
        try {
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }

            var videolist = MysqlSeed.loadLocalPhoto();
            if (videolist != null && (long) videolist.size() > 0) {
                List<PhotoEntity> list = MysqlSeed.paginateNews(videolist, pageNum, ApiConfig.PAGE_SIZE);
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
                        showToast("持续更新中...");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("PhotoFragment", e.getMessage(), e);
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }
        }
    }
}