package com.example.wechart.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtName;
    private EditText mEtCode;
    private EditText mEtClass;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnRegister;
    private SpUtils mSpUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEtName = findViewById(R.id.et_student_name);
        mEtCode = findViewById(R.id.et_student_code);
        mEtClass = findViewById(R.id.et_student_class);
        mEtUsername = findViewById(R.id.et_student_username);
        mEtPassword = findViewById(R.id.et_student_password);
        mBtnRegister = findViewById(R.id.register_button);
        mBtnRegister.setOnClickListener(this);
        mSpUtils = new SpUtils(this);
    }

    @Override
    public void onClick(View v) {//注册按钮点击事件
        String name = mEtName.getText().toString();
        String code = mEtCode.getText().toString();
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String className = mEtClass.getText().toString();

        //判断输入的内容都不能是空
        if (allInputNotEmpty(name, code, username, password, className)) {
            Student student = new Student();
            student.setStudent_code(code);
            student.setStudent_name(name);
            student.setUsername(username);
            student.setPassword(password);
            student.setClass_name(className);
            //将新注册的学生信息保存到数据库
            int id = SqliteHelper.getInstance(this).insertStudent2(student);
            //记录当前登录账号的id
            mSpUtils.putInt(SpUtils.LOGIN_KEY, id);

            //注册成功，数据保存后，在这里跳转到主界面
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allInputNotEmpty(String... inputStrings) {
        for (int i = 0; i < inputStrings.length; i++) {
            if (TextUtils.isEmpty(inputStrings[i])) {
                return false;
            }
        }
        return true;
    }
}
