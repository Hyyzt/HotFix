package com.eulertu.opensdk.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.eulertu.opensdk.R;

public class WindowFloatingViewJava extends RelativeLayout {

    private int JUST_CLICK = 5;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private int slop = 0;
    private float xInScreen = 0f;
    private float yInScreen //当前手指位置
            = 0f;
    private float xDownInScreen = 0f;
    private float yDownInScreen //手指按下位置
            = 0f;
    private float xInView = 0f;
    private float yInView //手指相对于悬浮窗位置
            = 0f;
    private boolean isIntercept = false;

    private int pixelWidth = 0;
    private int pixelHeight = 0;

    public WindowFloatingViewJava(Context context) {
        this(context, null);
    }

    public WindowFloatingViewJava(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowFloatingViewJava(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        pixelHeight = localDisplayMetrics.heightPixels;
        if (pixelHeight >= 672 && pixelHeight <= 720) {
            pixelHeight = 720;
        }
        pixelWidth = localDisplayMetrics.widthPixels;
        if (pixelWidth == 1920) {
            pixelHeight = 1080;
        }
        LayoutInflater.from(context).inflate(R.layout.opensdk_demo_test_touch, this);
    }

    public void updateViewLayoutParams(WindowManager.LayoutParams params) {
        mLayoutParams = params;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                isIntercept = false;
            }
            case MotionEvent.ACTION_DOWN: {
                xInView = event.getX();//相对于view的坐标
                yInView = event.getY();

                //getRaw()返回相对于屏幕左上角坐标
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = xDownInScreen;
                yInScreen = yDownInScreen;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isIntercept) {
                    return true;
                }
                float newXInView = event.getRawX();
                float newYInView = event.getRawY();
                if (newXInView - xDownInScreen > slop || newYInView - yDownInScreen > slop) {
                    return true;
                }
            }
        }
        return true;
    }

    // 在此处重写 onTouchEvent 处理相应的事件
    // 必须返回 true 表示在此已处理相应事件

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                xInView = 0f;
                yInView = 0f;
                isIntercept = false;
                if (Math.abs(xDownInScreen - xInScreen) < JUST_CLICK && Math.abs(yDownInScreen - yInScreen) < JUST_CLICK) {
                    return true;
                }
                showAnimation();
            }
            case MotionEvent.ACTION_MOVE: {
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                updateViewPosition();
            }
        }
        return true;
    }

    private void updateViewPosition() {
        if (mLayoutParams != null) {
            mLayoutParams.x = (int) (xInScreen - xInView);
            mLayoutParams.y = (int) (yInScreen - yInView);
            mWindowManager.updateViewLayout(this, mLayoutParams);
        }
    }


    private void showAnimation() {
        int startPos = mLayoutParams.x;
        int result = 0;
        if ((mLayoutParams.x + getWidth() / 2) < pixelWidth / 2) {
            // 靠左
            result = -startPos;
        } else {
            // 靠右
            result = pixelWidth - getWidth() - startPos;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        int finalResult = result;
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float values = (float) valueAnimator1.getAnimatedValue();
            mLayoutParams.x = startPos + (int) (values * finalResult);
            mWindowManager.updateViewLayout(WindowFloatingViewJava.this, mLayoutParams);
        });
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(150);
        valueAnimator.start();
    }
}
