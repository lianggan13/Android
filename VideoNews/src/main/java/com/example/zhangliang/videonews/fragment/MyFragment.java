package com.example.zhangliang.videonews.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.zhangliang.videonews.R;
import com.example.zhangliang.videonews.activity.LoginActivity;
import com.example.zhangliang.videonews.activity.MyCollectActivity;

import skin.support.SkinCompatManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    //    @BindView(R.id.img_header)
    private ImageView imgHeader;
    private RelativeLayout rlCollect;
    private RelativeLayout rlSkin;
    private RelativeLayout rlLogout;

    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        imgHeader = mRootView.findViewById(R.id.img_header);
        rlCollect = mRootView.findViewById(R.id.rl_collect);
        rlSkin = mRootView.findViewById(R.id.rl_skin);
        rlLogout = mRootView.findViewById(R.id.rl_logout);

        imgHeader.setOnClickListener(this);
        rlCollect.setOnClickListener(this);
        rlSkin.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }

    //    @OnClick({R.id.img_header, R.id.rl_collect, R.id.rl_skin, R.id.rl_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_header:

                break;
            case R.id.rl_collect:
                navigateTo(MyCollectActivity.class);
                break;
            case R.id.rl_skin:
                String skin = findByKey("skin");
                if (skin.equals("night")) {
                    // 恢复应用默认皮肤
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                    insertVal("skin", "default");
                } else {
                    SkinCompatManager.getInstance().loadSkin("night", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
                    insertVal("skin", "night");
                }
                break;
            case R.id.rl_logout:
                removeByKey("token");
                navigateToWithFlag(LoginActivity.class,
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
        }
    }


}