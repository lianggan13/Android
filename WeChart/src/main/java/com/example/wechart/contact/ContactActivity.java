package com.example.wechart.contact;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wechart.R;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.rv_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getPermissions();
    }

    String[] permissionList;

    //申请权限
    public void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList = new String[]{"android.permission.READ_CONTACTS"};
            ArrayList<String> list = new ArrayList<>();
            for (String s : permissionList) {
                if (ActivityCompat.checkSelfPermission(ContactActivity.this, s) !=
                        PackageManager.PERMISSION_GRANTED) {
                    list.add(s);
                }
            }
            if (list.size() > 0) {
                ActivityCompat.requestPermissions(ContactActivity.this, list.toArray(list.toArray(new String[list.size()])), 1);
            } else {
                setDate();
            }
        } else {
            setDate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.READ_CONTACTS") &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "读取通讯录权限申请成功", Toast.LENGTH_SHORT).show();
                    setDate();
                } else {
                    Toast.makeText(this, "读取通讯录权限申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setDate() {
        List<ContactInfo> contactInfoList = getContacts();
        ContactAdapter adapter = new ContactAdapter(ContactActivity.this, contactInfoList);
        recyclerView.setAdapter(adapter);
    }

    //获取通讯录数据
    private List<ContactInfo> getContacts() {
        List<ContactInfo> contactInfos = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
            @SuppressLint("Range") String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            @SuppressLint("Range") int isHas = cursor.getInt(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (isHas > 0) {
                Cursor cursor1 = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null, null);
                while (cursor1.moveToNext()) {
                    ContactInfo contactInfo = new ContactInfo();
                    contactInfo.setContactName(name);
                    @SuppressLint("Range") String number = cursor1.getString(
                            cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    contactInfo.setPhoneNumber(number);
                    contactInfos.add(contactInfo);
                }
                cursor1.close();
            }
        }
        cursor.close();
        return contactInfos;
    }
}