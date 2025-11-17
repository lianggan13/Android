package com.example.zhangliang.videoprojects.ui.video;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zhangliang.videoprojects.R;
import com.example.zhangliang.videoprojects.adapter.VideoAdapter;
import com.example.zhangliang.videoprojects.api.ApiConfig;
import com.example.zhangliang.videoprojects.database.MysqlSeed;
import com.example.zhangliang.videoprojects.entity.VideoEntity;
import com.example.zhangliang.videoprojects.listener.OnItemChildClickListener;
import com.example.zhangliang.videoprojects.ui.BaseFragment;
import com.example.zhangliang.videoprojects.util.Tag;
import com.example.zhangliang.videoprojects.util.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.VideoView;

public class VideoFragment extends BaseFragment implements OnItemChildClickListener {

    private int categoryId;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private int pageNum = 1;
    private VideoAdapter videoAdapter;
    private List<VideoEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    protected VideoView mVideoView;
    protected StandardVideoController mController;
    protected ErrorView mErrorView;
    protected CompleteView mCompleteView;
    protected TitleView mTitleView;
    /**
     * 当前播放的位置
     */
    protected int mCurPos = -1;
    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    protected int mLastPos = mCurPos;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    videoAdapter.setDatas(datas);
                    videoAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public VideoFragment() {
    }

    public static VideoFragment newInstance(int categoryId) {
        VideoFragment fragment = new VideoFragment();
        fragment.categoryId = categoryId;
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        initVideoView();
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);
    }

    @Override
    protected void initData() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        videoAdapter = new VideoAdapter(getActivity());
        videoAdapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                FrameLayout playerContainer = view.findViewById(R.id.player_container);
                if (playerContainer != null) {
                    View v = playerContainer.getChildAt(0);
                    if (v != null && v == mVideoView && !mVideoView.isFullScreen()) {
                        releaseVideoView();
                    }
                }
            }
        });

//        refreshLayout.setRefreshHeader(new ClassicsHeader(requireContext()));
//        refreshLayout.setRefreshFooter(new ClassicsFooter(requireContext()));

//        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
//        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

//        refreshLayout.setRefreshHeader(new BezierRadarHeader(requireContext())
//                .setEnableHorizontalDrag(true));
//        refreshLayout.setRefreshFooter(new BallPulseFooter(requireContext())
//                .setSpinnerStyle(SpinnerStyle.FixedFront));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNum = 1;
                getVideoListLocal(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageNum++;
                getVideoListLocal(false);
            }
        });
        getVideoListLocal(true);
    }

    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mVideoView.setOnStateChangeListener(new BaseVideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                // 监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mLastPos = mCurPos;
                    mCurPos = -1;
                }
            }
        });
        mController = new StandardVideoController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(getActivity()));
        mController.addControlComponent(new GestureView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    /**
     * 由于onPause必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onPause的逻辑
     */
    protected void pause() {
        releaseVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }

    /**
     * 由于onResume必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onResume的逻辑
     */
    protected void resume() {
        if (mLastPos == -1)
            return;
        // 恢复上次播放的位置
        startPlay(mLastPos);
    }

    /**
     * PrepareView被点击
     */
    @Override
    public void onItemChildClick(int position) {
        startPlay(position);
    }

    /**
     * 开始播放
     *
     * @param position 列表位置
     */
    protected void startPlay(int position) {
        if (mCurPos == position) return;
        if (mCurPos != -1) {
            releaseVideoView();
        }
        VideoEntity videoEntity = datas.get(position);

        // 边播边存
        // String proxyUrl = ProxyVideoCacheManager.getProxy(getActivity()).getProxyUrl(videoBean.getUrl());
        // mVideoView.setUrl(proxyUrl);

        var title = videoEntity.getVtitle();
        var url = videoEntity.getPlayurl();

        if (url.toLowerCase().startsWith("http") && url.toLowerCase().endsWith(".mp4")) {
            // 网络视频流
            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        } else if (url.toLowerCase().startsWith("rtsp:")) {
            // RTSP 视频
            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
        } else if (url.toLowerCase().endsWith(".mp4")) {
            // 本地视频文件 (视频在 assets 目录下)
            try {
                String assetName = url;
                boolean assetUncompressed = true;
                try {
                    // 如果 asset 被压缩，openFd 会抛异常
                    requireContext().getAssets().openFd(assetName).close();
                } catch (Exception afdEx) {
                    assetUncompressed = false;
                    Log.w("VideoFragment", "asset openFd failed (可能被压缩)，fallback to copy: " + assetName, afdEx);
                }

                if (assetUncompressed) {
                    // ExoPlayer 支持 asset scheme（asset:///yourfile.mp4）
                    mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
                    String assetUri = "asset:///" + assetName;
                    url = assetUri;
                } else {
                    // ：拷贝到缓存（仅在 asset 被压缩时）
                    java.io.File outFile = new java.io.File(requireContext().getCacheDir(), assetName);
                    if (!outFile.exists() || outFile.length() == 0) {
                        try (java.io.InputStream in = requireContext().getAssets().open(assetName);
                             java.io.OutputStream out = new java.io.FileOutputStream(outFile)) {
                            byte[] buf = new byte[8192];
                            int len;
                            while ((len = in.read(buf)) != -1) {
                                out.write(buf, 0, len);
                            }
                            out.flush();
                        } catch (Exception e) {
                            Log.e("VideoFragment", "Copy asset to cache failed: " + assetName, e);
                        }
                    }
                    if (outFile.exists() && outFile.length() > 0) {
                        mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
                        url = outFile.getAbsolutePath();
                    } else {
                        Log.e("VideoFragment", "无法获取播放源: " + assetName);
                        showToast("无法播放本地视频：" + assetName);
                    }
                }
            } catch (Exception e) {
                Log.e("VideoFragment", "本地视频播放失败", e);
                showToast("本地视频播放失败");
            }
        } else {
            // 其他情况一律按网络视频流处理
            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        }

        mVideoView.setUrl(url);
        mTitleView.setTitle(title);
        View itemView = linearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;

        VideoAdapter.ViewHolder viewHolder = (VideoAdapter.ViewHolder) itemView.getTag();
        // 把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController.addControlComponent(viewHolder.mPrepareView, true);
        Utils.removeViewFormParent(mVideoView);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        // 播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        getVideoViewManager().add(mVideoView, Tag.LIST);
        mVideoView.start();
        mCurPos = position;
    }

    private void releaseVideoView() {
        if (mVideoView == null) return;
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPos = -1;
    }

    private void getVideoListLocal(final boolean isRefresh) {
        // 分页 + 弹性刷新
        try {
            var videolist = MysqlSeed.loadLocalVideos();
            if (videolist != null && (long) videolist.size() > 0) {
                List<VideoEntity> list = MysqlSeed.paginateVideos(videolist, pageNum, ApiConfig.PAGE_SIZE, categoryId);
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
            Log.e("VideoFragment", e.getMessage(), e);
        } finally {
            if (isRefresh) {
                refreshLayout.finishRefresh(true);
            } else {
                refreshLayout.finishLoadMore(true);
            }
        }
    }
}