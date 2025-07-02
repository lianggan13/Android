package com.example.zhangliang.videoprojects.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zhangliang.videoprojects.adapter.NewsAdapter;
import com.example.zhangliang.videoprojects.database.MysqlSeed;
import com.example.zhangliang.videoprojects.databinding.FragmentHomeBinding;
import com.example.zhangliang.videoprojects.entity.NewsEntity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private NewsAdapter newsAdapter;
    private int pageNum = 1;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    //    @Override
    protected void initView() {
        recyclerView = binding.recyclerView;
        refreshLayout = binding.refreshLayout;
        homeViewModel.getNewsList().observe(getViewLifecycleOwner(), list -> {
            newsAdapter.setDatas(list);
            newsAdapter.notifyDataSetChanged();
        });
    }

    //    @Override
    protected void initData() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
                getNewsListByLocal(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageNum++;
                getNewsListByLocal(false);
            }
        });
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
            if (videolist != null && !videolist.isEmpty()) {
                List<NewsEntity> list = MysqlSeed.paginateNews(videolist, pageNum, 10);
                if (list != null && !list.isEmpty()) {
                    List<NewsEntity> currentList;
                    if (isRefresh) {
                        currentList = new ArrayList<>(list);
                    } else {
                        // 合并旧数据和新数据
                        currentList = new ArrayList<>();
                        List<NewsEntity> old = homeViewModel.getNewsList().getValue();
                        if (old != null) currentList.addAll(old);
                        currentList.addAll(list);
                    }
                    // 用 LiveData 通知 UI 自动刷新
                    homeViewModel.setNewsList(currentList);
                } else {
                    showToast(isRefresh ? "暂时无数据" : "没有更多数据");
                }
            } else {
                showToast("暂无本地数据");
            }
        } catch (Exception e) {
            Log.e("HomeFragment", e.getMessage(), e);
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}