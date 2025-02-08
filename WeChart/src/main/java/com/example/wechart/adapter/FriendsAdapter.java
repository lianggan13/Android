package com.example.wechart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wechart.R;
import com.example.wechart.bean.Student;

import java.util.List;

public class FriendsAdapter extends BaseAdapter {

    private List<Student> mList;
    private LayoutInflater mLayoutInflater;

    public FriendsAdapter(List<Student> list, Context context) {
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    //刷新数据，传入新的数据列表
    public void updateData(List<Student> list) {
        mList.clear();
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Student getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.friend_item, null);
            viewHolder.idText = convertView.findViewById(R.id.student_id);
            viewHolder.nameText = convertView.findViewById(R.id.student_name);
            viewHolder.codeText = convertView.findViewById(R.id.student_code);
            viewHolder.usernameText = convertView.findViewById(R.id.username);
            viewHolder.classnameText = convertView.findViewById(R.id.class_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Student student = getItem(position);
        if (student != null) {
            convertView.setTag(R.integer.friend_tag, student);
            viewHolder.idText.setText(String.valueOf(student.getStudent_id()));
            viewHolder.nameText.setText(student.getStudent_name());
            viewHolder.codeText.setText(student.getStudent_code());
            viewHolder.usernameText.setText(student.getUsername());
            viewHolder.classnameText.setText(student.getClass_name());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView idText;
        TextView nameText;
        TextView codeText;
        TextView usernameText;
        TextView classnameText;
    }
}
