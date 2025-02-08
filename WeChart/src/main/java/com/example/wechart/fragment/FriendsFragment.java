package com.example.wechart.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.wechart.R;
import com.example.wechart.adapter.FriendsAdapter;
import com.example.wechart.bean.Student;
import com.example.wechart.component.EmptyView;
import com.example.wechart.component.HeaderView;
import com.example.wechart.database.SqliteHelper;
import com.example.wechart.fragment.base.BaseFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends BaseFragment implements View.OnClickListener {

    private HeaderView mHeaderView;
    private ListView mLvFriends;
    private EmptyView mEmptyView;
    private List<Student> mFriendsList;
    private FriendsAdapter mFriendsAdapter;
    private AlertDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friends;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        mHeaderView = mView.findViewById(R.id.header_view);
        mHeaderView.setTitle("我的同学");
        mLvFriends = mView.findViewById(R.id.lv_friends);
        mEmptyView = mView.findViewById(R.id.empty_view);
        //ListView中没有数据时，显示EmptyView
        mLvFriends.setEmptyView(mEmptyView);
        //数据库查询学生列表数据，显示在学生列表界面
        mFriendsList = SqliteHelper.getInstance(mContext).selectAllStudent();
        mFriendsAdapter = new FriendsAdapter(mFriendsList, mContext);
        mLvFriends.setAdapter(mFriendsAdapter);
        //短按修改student_name
        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) view.getTag(R.integer.friend_tag);
                SqliteHelper.getInstance(mContext).updateStudentNameById(student.getStudent_name() + "s", student.getStudent_id());
                refreshStudentList();
            }
        });
        //长按学生列表的条目
        mLvFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) view.getTag(R.integer.friend_tag);
                //弹框提示是否删除
                showDeleteDialog2(student);
                return true;
            }
        });
    }

    @Override
    protected void initListener() {
        mHeaderView.setRightClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_right) {
            addFriends();
        }
    }

    //系统弹框
    private void showDeleteDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除");
        builder.setMessage("确定要删除吗？");
        builder.setIcon(R.drawable.attention);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SqliteHelper.getInstance(mContext).deleteStudentById(student.getStudent_id());
                showToast(student.getStudent_name() + "信息被删除");
                refreshStudentList();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    //自定义布局弹框
    private void showDeleteDialog2(Student student) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_attention, null);
        TextView message = v.findViewById(R.id.message);
        Button btn_sure = v.findViewById(R.id.button_right);
        Button btn_cancel = v.findViewById(R.id.button_left);
        message.setText("真的要删吗？");
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认删除，关闭弹框
                mDialog.cancel();
                //数据库中删除
                SqliteHelper.getInstance(mContext).deleteStudentById(student.getStudent_id());
                showToast(student.getStudent_name() + "信息被删除");
                //删除后刷新列表
                refreshStudentList();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialog = builder.create();
        //mDialog.setView(v);
        mDialog.show();
        Window window = mDialog.getWindow();
        //width 你想要的宽度； height 你想要的高度
        window.setLayout(840, 600);
        window.setContentView(v);
    }

    //刷新列表：重新去数据库中查询数据，并将数据设置到Adapter中。
    private void refreshStudentList() {
        mFriendsList = SqliteHelper.getInstance(mContext).selectAllStudent();
        mFriendsAdapter.updateData(mFriendsList);
    }

    private void addFriends() {
        Log.d(TAG, "addFriends");
        Student student = new Student();
        student.setStudent_code("202100000005");
        student.setStudent_name("jim");
        student.setUsername("sui");
        student.setPassword("323654587");
        student.setClass_name("大数据1班");
        mFriendsList.add(student);
        SqliteHelper.getInstance(mContext).insertStudent(student);

        Student student2 = new Student();
        student2.setStudent_code("202100000006");
        student2.setStudent_name("tom");
        student2.setUsername("bobo");
        student2.setPassword("98989888");
        student2.setClass_name("大数据2班");
        mFriendsList.add(student2);
        SqliteHelper.getInstance(mContext).insertStudent2(student2);
        //当ListView的数据源发生变化时，调用Adapter的notifyDataSetChanged，刷新界面
        //mFriendsAdapter.notifyDataSetChanged();
        refreshStudentList();
    }
}
