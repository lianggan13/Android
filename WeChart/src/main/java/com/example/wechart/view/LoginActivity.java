package com.example.wechart.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wechart.MainActivity;
import com.example.wechart.R;
import com.example.wechart.bean.Student;
import com.example.wechart.database.SqliteHelper;
import com.example.wechart.utility.SpUtils;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnRegister;
    private Button mBtnLogin;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpUtils = new SpUtils(this);
        int loginId = mSpUtils.getInt(SpUtils.LOGIN_KEY, -1);
        Log.d("", "login id = " + loginId);
        //从桌面进入程序，如果已经登录，不显示登录界面，直接跳转到主界面。
        if (loginId >= 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_login);
        mEtUsername = findViewById(R.id.et_student_username);
        mEtPassword = findViewById(R.id.et_student_password);
        mBtnRegister = findViewById(R.id.register_button);
        mBtnLogin = findViewById(R.id.login_button);
        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.register_button) { //点击注册按钮，跳转到注册界面
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.login_button) { //点击登录按钮
            String username = mEtUsername.getText().toString();
            String password = mEtPassword.getText().toString();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) { //用户名或密码没有输入，Toast提示。
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            //数据库中查询用户名和密码都匹配的数据
            Student student = SqliteHelper.getInstance(this).login(username, password);
            if (student == null) { //没有匹配的数据，登录失败，Toast提示。
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            } else {//登录成功跳转到主界面
                mSpUtils.putInt(SpUtils.LOGIN_KEY, student.getStudent_id());//记录当前登录学生的id
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
