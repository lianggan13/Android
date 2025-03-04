package com.learn.zhangliang.userview.Animation;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.learn.zhangliang.userview.R;

/**
 * Created by qijian on 16/12/18.
 */
public class AnimateLayoutChangesActivity extends Activity {
    private LinearLayout linearLayoutContainer;

    private int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animate_layout_changes_activity);
        linearLayoutContainer = (LinearLayout) findViewById(R.id.linearlayoutcontainer);

        LayoutTransition transition = new LayoutTransition();
        //入场动画:view 在这个容器中消失时触发的动画
        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 0f, 360f, 0f);
        transition.setAnimator(LayoutTransition.APPEARING, animIn);

        //出场动画:view 显示时的动画
        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotation", 0f, 90f, 0f);
        transition.setAnimator(LayoutTransition.DISAPPEARING, animOut);

        //出现移动动画：在容器中添加 View 时，其他已有 View 需要移动时的动画
        // 在构造 PropertyValuesHolder 动画时，"left" 和 "top" 属性的变动是必写的，因为它们确保了在视图添加或移除时，动画能够正确反映视图的移动和缩放，从而保持布局的连贯性和视觉一致性
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 0);
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        Animator changeAppearAnimator
                = ObjectAnimator.ofPropertyValuesHolder(linearLayoutContainer, pvhLeft, pvhTop, pvhScaleX);
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, changeAppearAnimator);

        //消失移动动画：定在容器中删除 View 时，其他已有 View 需要移动时的动画
//        LayoutTransition transition = new LayoutTransition();
        PropertyValuesHolder outLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        PropertyValuesHolder outTop = PropertyValuesHolder.ofInt("top", 0, 0);

        Keyframe frame0 = Keyframe.ofFloat(0f, 0);
        Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
        Keyframe frame2 = Keyframe.ofFloat(0.2f, 20f);
        Keyframe frame3 = Keyframe.ofFloat(0.3f, -20f);
        Keyframe frame4 = Keyframe.ofFloat(0.4f, 20f);
        Keyframe frame5 = Keyframe.ofFloat(0.5f, -20f);
        Keyframe frame6 = Keyframe.ofFloat(0.6f, 20f);
        Keyframe frame7 = Keyframe.ofFloat(0.7f, -20f);
        Keyframe frame8 = Keyframe.ofFloat(0.8f, 20f);
        Keyframe frame9 = Keyframe.ofFloat(0.9f, -20f);
        Keyframe frame10 = Keyframe.ofFloat(1, 0);
        PropertyValuesHolder mPropertyValuesHolder = PropertyValuesHolder.ofKeyframe("rotation", frame0, frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8, frame9, frame10);

        ObjectAnimator mObjectAnimatorChangeDisAppearing = ObjectAnimator.ofPropertyValuesHolder(this, outLeft, outTop, mPropertyValuesHolder);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mObjectAnimatorChangeDisAppearing);

        // 设置动画监听
        transition.addTransitionListener(new LayoutTransition.TransitionListener() {
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                Log.d("Transition", "start:" + "transitionType:" + transitionType + "count:" + container.getChildCount() + "view:" + view.getClass().getName());
            }

            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                Log.d("Transition", "end:" + "transitionType:" + transitionType + "count:" + container.getChildCount() + "view:" + view.getClass().getName());
            }
        });

        linearLayoutContainer.setLayoutTransition(transition);

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addButtonView();
            }
        });
        findViewById(R.id.remove_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeButtonView();
            }
        });
    }

    private void addButtonView() {
        i++;
        Button button = new Button(this);
        button.setText("button" + i);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        linearLayoutContainer.addView(button, 0);
    }

    private void removeButtonView() {
        if (i > 0) {
            linearLayoutContainer.removeViewAt(0);
        }
        i--;
    }

}
