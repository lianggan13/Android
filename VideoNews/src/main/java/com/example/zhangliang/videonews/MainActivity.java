package com.example.zhangliang.videonews;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.example.zhangliang.videonews.activity.BaseActivity;
import com.example.zhangliang.videonews.activity.HomeActivity;
import com.example.zhangliang.videonews.activity.LoginActivity;
import com.example.zhangliang.videonews.activity.RegisterActivity;
import com.example.zhangliang.videonews.util.StringUtils;

public class MainActivity extends BaseActivity {

    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    @Override
    protected void initData() {
        if (!StringUtils.isEmpty(findByKey("token"))) {
            navigateTo(HomeActivity.class);
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(in);
                navigateTo(LoginActivity.class);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in = new Intent(MainActivity.this, RegisterActivity.class);
//                startActivity(in);
                navigateTo(RegisterActivity.class);
            }
        });
    }


    @Override
    public void addMenuProvider(@NonNull MenuProvider provider, @NonNull LifecycleOwner owner, @NonNull Lifecycle.State state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止方法追踪
//        Debug.stopMethodTracing();
    }
}