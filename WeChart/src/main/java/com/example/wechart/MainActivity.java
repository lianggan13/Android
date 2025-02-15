package com.example.wechart;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wechart.bean.Student;
import com.example.wechart.database.SqliteHelper;
import com.example.wechart.databinding.ActivityMainBinding;
import com.example.wechart.fragment.AddressBookFragment;
import com.example.wechart.fragment.CoursesFragment;
import com.example.wechart.fragment.DiscoveryFragment;
import com.example.wechart.fragment.FriendsFragment;
import com.example.wechart.fragment.HomeFragment;
import com.example.wechart.fragment.MeFragment;
import com.example.wechart.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //定义Fragment列表用于切换
    List<Fragment> fragmentList;
    //定义底部导航栏用于切换
    BottomNavigationView bottomNavigationView;

    private ImageView mIvFriends;
    private ImageView mIvCourses;
    private ImageView mIvMine;
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private FriendsFragment mFriendsFragment;
    private CoursesFragment mCoursesFragment;
    private MineFragment mMineFragment;
    private @NonNull ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        HttpClient httpclient = new HttpClient();
//        httpclient.requestOkhttp("https://www.metools.info");
        //初始化fragmentList
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new AddressBookFragment());
        fragmentList.add(new DiscoveryFragment());
        fragmentList.add(new MeFragment());
        //默认显示第一个首页fragment
        replaceFragment(fragmentList.get(0));
        //找到底部导航栏id
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //底部导航栏点击时触发
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                //用switch报错，改用if实现
                if (itemId == R.id.menu_message) {
                    replaceFragment(fragmentList.get(0));//显示微信
                } else if (itemId == R.id.menu_address_book) {
                    replaceFragment(fragmentList.get(1));//显示通讯录
                } else if (itemId == R.id.menu_discovery) {
                    replaceFragment(fragmentList.get(2));//显示发现
                } else {
                    replaceFragment(fragmentList.get(3));//显示我的
                }
                return true;
            }
        });

        mIvFriends = findViewById(R.id.iv_friends);
        mIvCourses = findViewById(R.id.iv_courses);
        mIvMine = findViewById(R.id.iv_mine);
        mIvFriends.setOnClickListener(this);
        mIvCourses.setOnClickListener(this);
        mIvMine.setOnClickListener(this);
        initFragment();
        changeFragment(mFriendsFragment);
        mFragmentManager.beginTransaction()
                .add(R.id.fragment_content, mFriendsFragment).addToBackStack(null).commitAllowingStateLoss();
        mCurrentFragment = mFriendsFragment;
        mIvFriends.setSelected(true);

        List<Student> list = SqliteHelper.getInstance(this).selectAllStudent();
        Log.d("", "LIST ==== " + list.size());

    }

    //显示fragment


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_friends) {
            //点击friends图标，friends图标设置为选中状态，
            //courses和mine图标设置为非选中状态，选中状态图标颜色发生变化，
            //图标在drawable中的selector文件配置。
            mIvFriends.setSelected(true);
            mIvCourses.setSelected(false);
            mIvMine.setSelected(false);
            changeFragment(mFriendsFragment);
        } else if (id == R.id.iv_courses) {
            mIvFriends.setSelected(false);
            mIvCourses.setSelected(true);
            mIvMine.setSelected(false);
            changeFragment(mCoursesFragment);
        } else if (id == R.id.iv_mine) {
            mIvFriends.setSelected(false);
            mIvCourses.setSelected(false);
            mIvMine.setSelected(true);
            changeFragment(mMineFragment);
        }
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFriendsFragment = new FriendsFragment();
        mCoursesFragment = new CoursesFragment();
        mMineFragment = new MineFragment();
    }

    public void changeFragment(Fragment to) {
        // 检查当前 Fragment 是否存在且与目标 Fragment 不同
        if (mCurrentFragment != null && mCurrentFragment != to) {
            // 如果目标 Fragment 尚未添加到 FragmentManager
            if (!to.isAdded()) {
                mFragmentManager.beginTransaction()
                        .hide(mCurrentFragment) // 隐藏当前 Fragment
                        .add(R.id.fragment_content, to) // 添加目标 Fragment
                        .addToBackStack(null)
                        .commitAllowingStateLoss(); // 提交事务
            } else {
                // 如果目标 Fragment 已存在，显示它
                mFragmentManager.beginTransaction()
                        .hide(mCurrentFragment) // 隐藏当前 Fragment
                        .show(to) // 显示目标 Fragment
                        .commitAllowingStateLoss(); // 提交事务
            }
            mCurrentFragment = to; // 更新当前 Fragment
        }
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_content, fragment) // 替换当前 Fragment 不需要保留 Fragment 的状态，可以使用 replace 方法来替换当前 Fragment
                .commitAllowingStateLoss();

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//        // 替换Fragment
//        fragmentTransaction.replace(R.id.fragment_content, fragment);
//        // 提交事务
//        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SqliteHelper.getInstance(this).close();
    }
}