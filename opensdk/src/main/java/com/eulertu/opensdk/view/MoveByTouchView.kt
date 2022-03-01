package com.eulertu.opensdk.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.Math.abs

class MoveByTouchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // 左右最小边距
    protected var LEFT_RIGHT_MARGIN_MIN: Int = 0

    // 顶部最小边距
    protected var TOP_MARGIN_MIN = 0

    // 底部最小边距
    protected var BOTTOM_MARGIN_MIN = 0

    private var pixelWidth: Int = 0
    private var pixelHeight: Int = 0

    init {
        val localDisplayMetrics = DisplayMetrics()
        (getContext()
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            .getMetrics(localDisplayMetrics)
        pixelHeight = localDisplayMetrics.heightPixels
        if (pixelHeight in 672..720) {
            pixelHeight = 720
        }
        pixelWidth = localDisplayMetrics.widthPixels
        if (pixelWidth == 1920) {
            pixelHeight = 1080
        }
    }


    // 点击检测
    private val JUST_CLICK = 5

    //当前手指位置
    private var xInScreen = 0f
    private var yInScreen = 0f

    //手指按下位置
    private var xDownInScreen = 0f
    private var yDownInScreen = 0f

    //手指相对于悬浮窗位置
    private var xInView = 0f
    private var yInView = 0f

    private var slop = 0
    private var isCanMove = true
    private var isIntercept = false
    private var layoutParams: FrameLayout.LayoutParams? = null

    fun setInterceptTouchEvent(can: Boolean) {
        isCanMove = can
    }

    fun updateViewLayoutParams(params: FrameLayout.LayoutParams) {
        layoutParams = params
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isCanMove) {
            return false
        }
        val result = super.onInterceptTouchEvent(event)
        when (event.action) {
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
                    return true.also { isIntercept = it }
                }
            }
            MotionEvent.ACTION_UP -> isIntercept = false
        }
        return result
    }

    // 在此处重写 onTouchEvent 处理相应的事件
    // 必须返回 true 表示在此已处理相应事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isCanMove) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                isIntercept = false
                if (abs(xDownInScreen - xInScreen) < JUST_CLICK && abs(yDownInScreen - yInScreen) < JUST_CLICK) {
                    return false
                }
                // 手抬起的时候，贴边
                showAnimation()
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
        var leftMargin = (xInScreen - xInView).toInt()
        if (leftMargin < LEFT_RIGHT_MARGIN_MIN) {
            leftMargin = LEFT_RIGHT_MARGIN_MIN
        } else {
            val width = width
            if (leftMargin + width + LEFT_RIGHT_MARGIN_MIN > pixelWidth) {
                leftMargin =
                    (pixelWidth - width - LEFT_RIGHT_MARGIN_MIN).toInt()
            }
        }
        var topMargin = (yInScreen - yInView).toInt()
        if (topMargin < TOP_MARGIN_MIN) {
            topMargin = TOP_MARGIN_MIN
        } else {
            if (topMargin + height + BOTTOM_MARGIN_MIN > pixelHeight) {
                topMargin =
                    (pixelHeight - height - BOTTOM_MARGIN_MIN).toInt()
            }
        }
        layoutParams?.leftMargin = leftMargin
        layoutParams?.topMargin = topMargin
        setLayoutParams(layoutParams)
    }

    private fun showAnimation() {
        val startPos: Int = layoutParams?.leftMargin!!
        val result =
            if (layoutParams?.leftMargin ?: 0 + width / 2 + LEFT_RIGHT_MARGIN_MIN < pixelWidth / 2) {
                // 靠左
                -startPos + LEFT_RIGHT_MARGIN_MIN
            } else {
                // 靠右
                pixelWidth - LEFT_RIGHT_MARGIN_MIN - width - startPos
            }.toInt()
        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener { animation ->
            val values = animation.animatedValue as Float
            layoutParams?.leftMargin = startPos + (values * result).toInt()
            setLayoutParams(layoutParams)
        }
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 150
        valueAnimator.start()
    }

}