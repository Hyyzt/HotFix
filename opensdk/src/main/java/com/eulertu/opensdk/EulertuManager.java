package com.eulertu.opensdk;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.eulertu.opensdk.view.WindowFloatingView;

public
class EulertuManager {

    private Context mContext;

    private static EulertuManager instance;

    private EulertuManager() {
    }

    public static EulertuManager getInstance() {
        if (instance == null) {
            synchronized (EulertuManager.class) {
                if (instance == null) {
                    instance = new EulertuManager();
                }
            }
        }
        return instance;
    }

    public void setContext(Context content) {
        mContext = content;
    }

    public Context getContext() {
        return mContext;
    }

    private void checkContext() {
        if (mContext == null) {
            throw new NullPointerException("Please set context first!");
        }
    }


    public void showFloat() {
        checkContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(mContext)) {
                WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                WindowFloatingView moveByTouchView = new WindowFloatingView(mContext);
                WindowManager.LayoutParams mFloatingViewParams;
                mFloatingViewParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
                    mFloatingViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                    mFloatingViewParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    mFloatingViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }

                mFloatingViewParams.format = PixelFormat.RGBA_8888;
                mFloatingViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
                mFloatingViewParams.gravity = Gravity.LEFT | Gravity.TOP;
                mFloatingViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mFloatingViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mFloatingViewParams.x = 0;
                mFloatingViewParams.y = 0;
                moveByTouchView.updateViewLayoutParams(mFloatingViewParams);
                moveByTouchView.setLayoutParams(mFloatingViewParams);
                try {
                    windowManager.addView(moveByTouchView, mFloatingViewParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
