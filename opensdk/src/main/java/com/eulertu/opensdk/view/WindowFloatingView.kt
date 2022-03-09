package com.eulertu.opensdk.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.widget.RelativeLayout
import android.view.WindowManager
import android.view.LayoutInflater
import com.eulertu.opensdk.R
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import com.eulertu.opensdk.view.WindowFloatingView

/**
 * 音视频悬浮窗
 *
 * @author sym
 * @time 2018/12/21 10:32
 */
class WindowFloatingView : RelativeLayout {

    private var mWindowManager: WindowManager? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private val slop = 0
    private var xInScreen = 0f
    private var yInScreen //当前手指位置
            = 0f
    private var xDownInScreen = 0f
    private var yDownInScreen //手指按下位置
            = 0f
    private var xInView = 0f
    private var yInView //手指相对于悬浮窗位置
            = 0f
    private var isIntercept = false


    private var pixelWidth: Int = 0
    private var pixelHeight: Int = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val localDisplayMetrics = DisplayMetrics()
        mWindowManager?.defaultDisplay
            ?.getMetrics(localDisplayMetrics)
        pixelHeight = localDisplayMetrics.heightPixels
        if (pixelHeight in 672..720) {
            pixelHeight = 720
        }
        pixelWidth = localDisplayMetrics.widthPixels
        if (pixelWidth == 1920) {
            pixelHeight = 1080
        }
        LayoutInflater.from(context).inflate(R.layout.opensdk_demo_test_touch, this, true)
    }

    fun updateViewLayoutParams(params: WindowManager.LayoutParams?) {
        mLayoutParams = params
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> isIntercept = false
            MotionEvent.ACTION_DOWN -> {
                xInView = event.x //相对于view的坐标
                yInView = event.y

                //getRaw()返回相对于屏幕左上角坐标
                xDownInScreen = event.rawX
                yDownInScreen = event.rawY
                xInScreen = xDownInScreen
                yInScreen = yDownInScreen
            }
            MotionEvent.ACTION_MOVE -> {
                if (isIntercept) {
                    return true
                }
                val newXInView = event.rawX
                val newYInView = event.rawY
                if (newXInView - xDownInScreen > slop || newYInView - yDownInScreen > slop) {
                    return true
                }
            }
        }
        return true
    }

    // 在此处重写 onTouchEvent 处理相应的事件
    // 必须返回 true 表示在此已处理相应事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                isIntercept = false
                if (Math.abs(xDownInScreen - xInScreen) < JUST_CLICK && Math.abs(yDownInScreen - yInScreen) < JUST_CLICK) {
                    return false
                }
//                showAnimation()
            }
            MotionEvent.ACTION_MOVE -> {
                xInScreen = event.rawX
                yInScreen = event.rawY
                updateViewPosition()
            }
        }
        return true
    }

    private fun updateViewPosition() {
        mLayoutParams!!.x = (xInScreen - xInView).toInt()
        mLayoutParams!!.y = (yInScreen - yInView).toInt()
        mWindowManager!!.updateViewLayout(this, mLayoutParams)
    }


    private fun showAnimation() {
        val startPos: Int = mLayoutParams!!.x
        val result =
            if (mLayoutParams?.x ?: 0 + width / 2 < pixelWidth / 2) {
                // 靠左
                -startPos
            } else {
                // 靠右
                pixelWidth - width - startPos
            }.toInt()
        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener { animation ->
            val values = animation.animatedValue as Float
            mLayoutParams?.x = startPos + (values * result).toInt()
            mWindowManager?.updateViewLayout(this, mLayoutParams)
        }
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 150
        valueAnimator.start()
    }

    companion object {
        private const val TAG = "WindowFloatingView"
        private const val JUST_CLICK = 5
    }
}