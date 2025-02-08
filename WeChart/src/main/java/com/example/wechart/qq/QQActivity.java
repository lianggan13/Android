package com.example.wechart.qq;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.wechart.R;

//public class QQActivity extends AppCompatActivity {
public class QQActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText et_name, et_email, et_pwd;
    private Button btn_submit;
    private String name, email, pwd, sex, hobbies;
    private RadioGroup rg_sex;
    private CheckBox cb_sing, cb_dance, cb_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qqactivity);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void init() {
        // 初始化控件
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        rg_sex = findViewById(R.id.rg_sex);
        cb_sing = findViewById(R.id.cb_sing);
        cb_dance = findViewById(R.id.cb_dance);
        cb_read = findViewById(R.id.cb_read);
        btn_submit = findViewById(R.id.btn_submit);

        // 设置监听事件
        btn_submit.setOnClickListener(this);
        cb_sing.setOnCheckedChangeListener(this);
        cb_dance.setOnCheckedChangeListener(this);
        cb_read.setOnCheckedChangeListener(this);
        hobbies = new String();

        // 设置性别选项的监听事件
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_boy:
                        sex = "男";
                        break;
                    case R.id.rb_girl:
                        sex = "女";
                        break;
                }
            }
        });
    }

    /**
     * 获取用户输入的信息
     */
    private void getData() {
        name = et_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        pwd = et_pwd.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                getData();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(QQActivity.this, "请输入名字", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(QQActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(QQActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sex)) {
                    Toast.makeText(QQActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(hobbies)) {
                    Toast.makeText(QQActivity.this, "请选择兴趣爱好", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QQActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Log.i("QQActivity", "注册的用户信息：" +
                            "名字：" + name +
                            "，邮箱：" + email +
                            "，性别：" + sex +
                            "，兴趣爱好：" + hobbies);
                }
                break;
        }
    }

    /**
     * 监听兴趣爱好的点击事件
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String motion = buttonView.getText().toString(); // 获取选项的文本内容
        if (isChecked) {
            if (!hobbies.contains(motion)) {
                // 判断之前选择的内容是否与此次选择的不同
                hobbies = hobbies + motion;
            }
        } else {
            if (hobbies.contains(motion)) {
                hobbies = hobbies.replace(motion, ""); // 取消选择时移除
            }
        }
    }
}