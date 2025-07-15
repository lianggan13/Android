package com.yunda.safe.plct

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val navController by lazy {
        this.findNavController(R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root);

        if (isActionBarEnabled(this)) {
            appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        }

        binding.root.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                XLog.i("【全局点击监控】点击坐标: ($x, $y)")
                v.performClick()
            }
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
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