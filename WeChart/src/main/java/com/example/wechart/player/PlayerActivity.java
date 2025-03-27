package com.example.wechart.player;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.danikula.videocache.Logger;
import com.example.wechart.R;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.BuildConfig;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

public class PlayerActivity extends AppCompatActivity {

    public FrameLayout mPlayerContainer;
    public PrepareView mPrepareView;
    private VideoView mVideoView;
    private StandardVideoController mController;
    private TitleView mTitleView;
    private Button btnPlay, btnStop;
    private RadioButton btnMp4, btnRtsp;

    private EditText txtMp4, txtRstp;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setLogEnabled(BuildConfig.DEBUG) //调试的时候请打开日志，方便排错
                /** 软解，支持格式较多，可通过自编译so扩展格式，结合 {@link xyz.doikki.dkplayer.widget.videoview.IjkVideoView} 使用更佳 */
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setPlayerFactory(AndroidMediaPlayerFactory.create()) //不推荐使用，兼容性较差
                /** 硬解，支持格式看手机，请使用CpuInfoActivity检查手机支持的格式，结合 {@link xyz.doikki.dkplayer.widget.videoview.ExoVideoView} 使用更佳 */
//                .setPlayerFactory(ExoMediaPlayerFactory.create())
                // 设置自己的渲染view，内部默认TextureView实现
//                .setRenderViewFactory(SurfaceRenderViewFactory.create())
                // 根据手机重力感应自动切换横竖屏，默认false
//                .setEnableOrientation(true)
                // 监听系统中其他播放器是否获取音频焦点，实现不与其他播放器同时播放的效果，默认true
//                .setEnableAudioFocus(false)
                // 视频画面缩放模式，默认按视频宽高比居中显示在VideoView中
//                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
                // 适配刘海屏，默认true
//                .setAdaptCutout(false)
                // 移动网络下提示用户会产生流量费用，默认不提示，
                // 如果要提示则设置成false并在控制器中监听STATE_START_ABORT状态，实现相关界面，具体可以参考PrepareView的实现
//                .setPlayOnMobileNetwork(false)
                // 进度管理器，继承ProgressManager，实现自己的管理逻辑
//                .setProgressManager(new ProgressManagerImpl())
                .build());

//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
//        }

//
        Logger.setDebug(BuildConfig.DEBUG);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10000);
//        }

        mPlayerContainer = findViewById(R.id.player_container);
        mPrepareView = findViewById(R.id.prepare_view);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        btnMp4 = findViewById(R.id.btn_mp4);
        btnRtsp = findViewById(R.id.btn_rtsp);
        txtMp4 = findViewById(R.id.txt_mp4);
        txtRstp = findViewById(R.id.txt_rtsp);

        mVideoView = new VideoView(this);

        mController = new StandardVideoController(this);
        mController.addControlComponent(new ErrorView(this));
        mController.addControlComponent(new CompleteView(this));
        mController.addControlComponent(new GestureView(this));
        mTitleView = new TitleView(this);
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(this));
        mVideoView.setVideoController(mController);

        btnMp4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                url = txtMp4.getText().toString();
                mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
            }
        });

        btnRtsp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                url = txtRstp.getText().toString();
                mVideoView.setPlayerFactory(IjkPlayerFactory.create());
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (mVideoView.isPlaying())
                releaseVideoView();

            mVideoView.setUrl(url);
            mTitleView.setTitle(url);

            mController.addControlComponent(mPrepareView, true);
            removeViewFormParent(mVideoView);

            mPlayerContainer.addView(mVideoView, 0);
            //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
            getVideoViewManager().add(mVideoView, "List");

            mVideoView.start();
        });

        btnStop.setOnClickListener(v -> {
            releaseVideoView();
        });

        btnMp4.setChecked(true);
    }

    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void releaseVideoView() {
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if (this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) return;
        ViewParent parent = v.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(v);
        }
    }
}