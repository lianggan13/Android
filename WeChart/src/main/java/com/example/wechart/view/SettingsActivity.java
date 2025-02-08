package com.example.wechart.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.wechart.R;
import com.example.wechart.component.HeaderView;
import com.example.wechart.utility.SpUtils;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private HeaderView mHeaderView;
    private Button mBtnLogout;
    private SpUtils mSpUtils;
    private SwitchCompat mAdminSwitch;
    private RelativeLayout mRlAddStudent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mHeaderView = findViewById(R.id.header_view);
        mHeaderView.setRightVisibility(View.GONE);
        mHeaderView.setTitle("设置");

        mAdminSwitch = findViewById(R.id.admin_switch);
        mAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //开关监听，判断是否显示"添加学生"和"添加课程"
                showAdminViews(isChecked);
            }
        });
        mRlAddStudent = findViewById(R.id.add_student_view);
        mRlAddStudent.setOnClickListener(this);

        mBtnLogout = findViewById(R.id.logout_button);
        mBtnLogout.setOnClickListener(this);
        mSpUtils = new SpUtils(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.logout_button) { //退出登录
            mSpUtils.putInt(SpUtils.LOGIN_KEY, -1);
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.add_student_view) { //跳转添加学生界面
            Intent intent = new Intent(SettingsActivity.this, AddStudentActivity.class);
            startActivity(intent);
        } else if (id == R.id.add_course_view) { // 跳转添加课程界面
            Toast.makeText(this, "跳转添加课程界面", Toast.LENGTH_SHORT).show();

        }
    }

    private void showAdminViews(boolean show) {
        View view = findViewById(R.id.admin_views);
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
