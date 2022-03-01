package com.eulertu.opensdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.eulertu.opensdk.R;

public
class TestTouchView extends RelativeLayout {
    public TestTouchView(Context context) {
        this(context, null);
    }

    public TestTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.opensdk_demo_test_touch, this, true);
        MoveByTouchView moveByTouchView = findViewById(R.id.view_red_bag_rain_tips_layout);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        moveByTouchView.updateViewLayoutParams(layoutParams);
    }
}
