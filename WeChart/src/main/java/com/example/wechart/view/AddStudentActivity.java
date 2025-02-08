package com.example.wechart.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wechart.R;
import com.example.wechart.bean.Student;
import com.example.wechart.component.HeaderView;
import com.example.wechart.database.SqliteHelper;
import com.example.wechart.databinding.ActivityAddStudentBinding;


public class AddStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddStudentBinding binding;

    private HeaderView mHeaderView;
    private EditText mEtName;
    private EditText mEtCode;
    private EditText mEtClass;
    private EditText mEtUsername;
    private EditText mEtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_student);
        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mHeaderView = findViewById(R.id.title_bar);
        mHeaderView.setTitle("添加学生");
        mEtName = findViewById(R.id.et_student_name);
        mEtCode = findViewById(R.id.et_student_code);
        mEtClass = findViewById(R.id.et_student_class);
        mEtUsername = findViewById(R.id.et_student_username);
        mEtPassword = findViewById(R.id.et_student_password);

        mHeaderView.setRightClickListener(this);
        binding.addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {//右上角的添加图标的点击事件，点击开始添加
        String name = mEtName.getText().toString();
        String code = mEtCode.getText().toString();
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String className = mEtClass.getText().toString();

        //判断输入内容不能是空
        if (allInputNotEmpty(name, code, username, password, className)) {
            Student student = new Student();
            student.setStudent_code(code);
            student.setStudent_name(name);
            student.setUsername(username);
            student.setPassword(password);
            student.setClass_name(className);
            //将输入的数据插入数据库中
            int id = SqliteHelper.getInstance(this).insertStudent2(student);
            if (id >= 0) {
                Toast.makeText(this, "添加成功,可继续添加", Toast.LENGTH_SHORT).show();
                mEtName.setText("");
                mEtCode.setText("");
                mEtUsername.setText("");
                mEtPassword.setText("");
                mEtClass.setText("");
            }
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
