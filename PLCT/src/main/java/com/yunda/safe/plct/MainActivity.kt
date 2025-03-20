package com.yunda.safe.plct

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.yunda.safe.plct.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController
        get() = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // 启用边缘到边缘:应用的内容可以延伸到屏幕的边缘，去掉默认的状态栏和导航栏的内边距，从而提供更沉浸式的用户体验
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root);

        if (isActionBarEnabled(this)) {
            appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_gallery)
        if (isActionBarEnabled(this))
            return (NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp())
        else
            return navController.navigateUp() || super.onSupportNavigateUp()

    }

    private fun isActionBarEnabled(context: Context): Boolean {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowActionBar, typedValue, true)
        return typedValue.data != 0
    }

    public override fun onDestroy() {
        super.onDestroy()
    }
}