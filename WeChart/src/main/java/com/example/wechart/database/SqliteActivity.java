package com.example.wechart.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wechart.R;
import com.example.wechart.databinding.ActivitySqliteBinding;

public class SqliteActivity extends AppCompatActivity {

    private ActivitySqliteBinding binding;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sqlite);
        binding = ActivitySqliteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SqliteHelper myDbHelper = SqliteHelper.getInstance(this);
        db = myDbHelper.getWritableDatabase();

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    public void addUser(View view) {

        String name = binding.nameEt.getText().toString().trim();
        String age = binding.phoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age)) {
            Toast.makeText(this, "姓名或者年龄为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", age);
        db.insert("user", null, values);
    }

    public void findAll(View view) {
        binding.contentTv.setText("");
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            binding.contentTv.append("\n" + "姓名：" + cursor.getString(1) + "      电话：" + cursor.getString(2));
        }
    }

    public void updateUser(View view) {
        String phone = binding.phoneEt.getText().toString().trim();
        String name = binding.nameEt.getText().toString().trim();
        ContentValues values = new ContentValues();
        values.put("phone", phone);

        db.update("user", values, "name=?", new String[]{name});
    }

    public void deleteUser(View view) {
        String name = binding.nameEt.getText().toString().trim();
        db.delete("user", "name=?", new String[]{name});
        findAll(null);
    }

    //根据用户名查询用户
    public void findUserByName(View view) {
        String name = binding.nameEt.getText().toString().trim();
        Cursor cursor = db.query("user", null, "name=?", new String[]{name}, null, null, null);
        while (cursor.moveToNext()) {
            binding.contentTv.setText("姓名：" + cursor.getString(1) + "      电话：" + cursor.getString(2));
        }
    }
}


