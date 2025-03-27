package com.example.wechart.refresh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wechart.R;
import com.example.wechart.bean.Chat;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


public class RefreshActivity extends AppCompatActivity {
    List<Chat> chatList = new ArrayList<Chat>();
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    SmartRefreshLayout smartRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_refresh);

        //获取recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        //填充一些数据
        for (int i = 0; i < 10; ++i) {
            Chat chats = new Chat();
            chats.title = "便签" + i;
            chats.time = "12:" + (10 + i);
            chats.content = "开心的第" + i + "天";
            chatList.add(chats);
        }

        myAdapter = new MyAdapter();
        //填充布局文件
        recyclerView.setAdapter(myAdapter);
        /*设置布局文件的显示方式*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        smartRefreshLayout = findViewById(R.id.refresh_parent);

        //头部刷新样式
        smartRefreshLayout.setRefreshHeader(new BezierRadarHeader(this)
                .setEnableHorizontalDrag(true));
        //尾部刷新样式
        smartRefreshLayout.setRefreshFooter(new BallPulseFooter(this)
                .setSpinnerStyle(SpinnerStyle.FixedFront));

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                //刷新前要清空原先的数据
                chatList.clear();
                for (int i = 0; i < 10; ++i) {
                    Chat chats = new Chat();
                    chats.title = "下拉刷新" + i;
                    chats.time = "12:" + (10 + i);
                    chats.content = "开心的第" + i + "天";
                    chatList.add(chats);
                }
                /*重新刷新列表控件的数据*/
                myAdapter.notifyDataSetChanged();
                smartRefreshLayout.finishRefresh(1000);
            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                for (int i = 0; i < 10; ++i) {
                    Chat chats = new Chat();
                    chats.title = "上拉更多" + i;
                    chats.time = "12:" + (10 + i);
                    chats.content = "开心的第" + i + "天";
                    chatList.add(chats);
                }
                /*重新刷新列表控件的数据*/
                myAdapter.notifyDataSetChanged();
                smartRefreshLayout.finishLoadMore(1000);

            }
        });

    }

    /*泛型的使用，负责将布局文件复制n次*/
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        /*加载并返回布局文件  java->xml*/
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RefreshActivity.this).inflate(R.layout.item_chat, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }


        /*填充，修改布局里的控件内容*/
        /*1.可以将数据提前填充到一个数组中
         * 2.通过数组的下标获取到值，填充到控件中*/
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            Chat chat = chatList.get(position);

            holder.textView1.setText(chat.title);
            holder.textView2.setText(chat.time);
            holder.textView3.setText(chat.content);
            holder.constraintLayout.setOnClickListener(
                    (view) -> {
//                        Toast.makeText(request, "", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//                        startActivity(intent);
                    }
            );
        }

        /*将布局复制的次数，返回值为item显示的数量*/
        @Override
        public int getItemCount() {
            return chatList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        TextView textView3;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /*并不是直接使用findViewById,而是通过调用itemView
             * 如果直接使用findViewById，默认是在MainActivity中查找*/
            textView1 = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            constraintLayout = itemView.findViewById(R.id.rootView);
        }
    }

}


