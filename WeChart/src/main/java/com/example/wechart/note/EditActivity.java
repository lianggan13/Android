package com.example.wechart.note;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.wechart.databinding.ActivityEditBinding;

import com.example.wechart.R;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // 设置标题和返回键
        binding.backIv.setOnClickListener(v -> finish());
        binding.titleTv.setText("添加记录");

//        binding.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                binding.contentEt.setText("");
//            }
//        });

        // 清空输入框
//        binding.delete.setOnClickListener(v -> binding.contentEt.setText(""));

    }
}