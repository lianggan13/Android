package com.example.wechart.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wechart.R;
import com.example.wechart.bean.Student;
import com.example.wechart.database.SqliteHelper;
import com.example.wechart.fragment.base.BaseFragment;
import com.example.wechart.utility.SpUtils;
import com.example.wechart.view.SettingsActivity;


public class MineFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTvName;
    private TextView mTvId;
    private TextView mTvCode;
    private TextView mTvUsername;
    private TextView mTvClass;
    private RelativeLayout mSettingLayout;

    private SpUtils mSpUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        mSettingLayout = mView.findViewById(R.id.settingView);
        mTvName = mView.findViewById(R.id.my_name);
        mTvId = mView.findViewById(R.id.my_id);
        mTvCode = mView.findViewById(R.id.my_code);
        mTvUsername = mView.findViewById(R.id.username);
        mTvClass = mView.findViewById(R.id.class_name);
        mSpUtils = new SpUtils(mContext);
        // 查询当前登录帐号的id
        int loginId = mSpUtils.getInt(SpUtils.LOGIN_KEY, -1);
        if (loginId >= 0) {
            //根据id查询当前登录学生的信息
            Student student = SqliteHelper.getInstance(mContext).getStudentById(loginId);
            if (student != null) {
                //将当前登录的学生信息显示在界面上
                mTvName.setText(student.getStudent_name());
                mTvId.setText(String.valueOf(student.getStudent_id()));
                mTvCode.setText(student.getStudent_code());
                mTvUsername.setText(student.getUsername());
                mTvClass.setText(student.getClass_name());
            }
        }
    }

    @Override
    protected void initListener() {
        mSettingLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.settingView) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            startActivity(intent);
        }
    }
}
