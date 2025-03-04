package com.learn.zhangliang.userview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.learn.zhangliang.userview.Animation.AlipayActivity;
import com.learn.zhangliang.userview.Animation.AnimateLayoutChangesActivity;
import com.learn.zhangliang.userview.Animation.CameraStretchDemo;
import com.learn.zhangliang.userview.Animation.CharacterEvaluatorActivity;
import com.learn.zhangliang.userview.Animation.FallingBallActivity;
import com.learn.zhangliang.userview.Animation.FrameAnimXMLActivity;
import com.learn.zhangliang.userview.Animation.GetPosTanActivity;
import com.learn.zhangliang.userview.Animation.LoadingDemo;
import com.learn.zhangliang.userview.Animation.LoadingDemoActivity;
import com.learn.zhangliang.userview.Animation.PathMenuActivity;
import com.learn.zhangliang.userview.Animation.RingBellActivity;
import com.learn.zhangliang.userview.Animation.ScannerDemo;
import com.learn.zhangliang.userview.CanvasOperation.CircleOperationActivity;
import com.learn.zhangliang.userview.CanvasOperation.ClipRgnActivity;
import com.learn.zhangliang.userview.CanvasOperation.CustomCircleActivity;
import com.learn.zhangliang.userview.CanvasOperation.CustomViewActivity;
import com.learn.zhangliang.userview.Draw.AnimWaveActivity;
import com.learn.zhangliang.userview.Draw.BezierGestureTrackActivity;
import com.learn.zhangliang.userview.Layout.FlowLayoutActivity;
import com.learn.zhangliang.userview.PaintBasis.PathView_1_2_Activity;
import com.learn.zhangliang.userview.PaintBasis.RectPontView_1_1_4_Activity;
import com.learn.zhangliang.userview.PaintBasis.SpiderActivity;
import com.learn.zhangliang.userview.PaintBasis.Summarize_1_1_1_Activity;
import com.learn.zhangliang.userview.PaintBasis.Summarize_1_1_3_Activity;
import com.learn.zhangliang.userview.PaintBasis.intersect_1_1_4_Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        findViewById(R.id.summarize_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Summarize_1_1_1_Activity.class));
            }
        });

        findViewById(R.id.canvas_basis_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Summarize_1_1_3_Activity.class));
            }
        });
        findViewById(R.id.rect_point_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RectPontView_1_1_4_Activity.class));
            }
        });

        findViewById(R.id.intersect_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, intersect_1_1_4_Activity.class));
            }
        });

        findViewById(R.id.path_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PathView_1_2_Activity.class));
            }
        });
        findViewById(R.id.spider_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SpiderActivity.class));
            }
        });


        findViewById(R.id.canvas_operation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CircleOperationActivity.class));
            }
        });


        findViewById(R.id.canvas_circle_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomCircleActivity.class));
            }
        });

        findViewById(R.id.clip_rgn_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClipRgnActivity.class));
            }
        });

        findViewById(R.id.custom_view_xml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomViewActivity.class));
            }
        });

        findViewById(R.id.camera_stretch_activity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraStretchDemo.class));
            }
        });

        findViewById(R.id.loading_demo_activity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoadingDemo.class));
            }
        });

        findViewById(R.id.scanner_demo_activity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScannerDemo.class));
            }
        });

        findViewById(R.id.xml_create).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FrameAnimXMLActivity.class));
            }
        });

        findViewById(R.id.loading_demo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoadingDemoActivity.class));
            }
        });

        findViewById(R.id.character_evaluator).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CharacterEvaluatorActivity.class));
            }
        });

        findViewById(R.id.falling_ball).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FallingBallActivity.class));
            }
        });

        findViewById(R.id.path_menu_activity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PathMenuActivity.class));
            }
        });

        findViewById(R.id.ring_bell_activity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RingBellActivity.class));
            }
        });

        findViewById(R.id.animateLayoutChangesActivity).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AnimateLayoutChangesActivity.class));
            }
        });

        findViewById(R.id.alipay_demo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AlipayActivity.class));
            }
        });

        findViewById(R.id.getpostan_demo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GetPosTanActivity.class));
            }
        });

        findViewById(R.id.bezier_gesture_track).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BezierGestureTrackActivity.class));
            }
        });

        findViewById(R.id.anim_wave_demo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AnimWaveActivity.class));
            }
        });

        findViewById(R.id.flowlayout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FlowLayoutActivity.class));
            }
        });


    }
}