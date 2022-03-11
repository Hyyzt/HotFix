package com.eulertu.opensdk

import android.app.Application
import android.util.Log
import com.feelschaotic.sdkhotfix.sdk.HotfixConfig
import com.feelschaotic.sdkhotfix.sdk.HotfixManager
import com.feelschaotic.sdkhotfix.sdk.statistics.StatisticsListener
import com.feelschaotic.sdkhotfix.sdk.utils.LogUtils
import java.lang.ref.WeakReference
import java.util.*

/**
 * 我是一个业务方示例sdk，我接入了hotfix库
 * @author feelschaotic
 * @create 2019/9/30.
 */
object SdkManager {
    private const val TAG = "SdkManager"
    private var applicationRef: WeakReference<Application>? = null

    fun init(application: Application) {
        applicationRef = WeakReference(application)
        Log.d(TAG, "init: 初始化热修复hotfixSdk")
        Log.e(TAG, "init: " + BuildConfig.APPLICATION_ID)
    }

    fun fixBug() {
        Log.e(TAG, "fixBug: ")

        applicationRef?.get()?.let {
            val hotfixConfig = HotfixConfig.Builder()
                .debug(true)//为true开启内部log打印
                .packageName(BuildConfig.APPLICATION_ID + "_" + BuildConfig.SDK_ARTIFACT_ID)//视情况加上ARTIFACT_ID 有时候会出现业务方sdk需要提供多ARTIFACT_ID的情况
                .appVersion(BuildConfig.SDK_VERSION)
                .statisticsListener(object : StatisticsListener {
                    override fun onTrack(eventName: String, eventParams: HashMap<String, Any>?) {
                        Log.d("TAG", "热更埋点触发 $eventName $eventParams")
                    }
                }).build()
            HotfixManager.init(it, hotfixConfig)
        }
    }

    // TODO: 2022/3/9 需要打补丁包时使用@Modify、@Add
//    @Modify
    fun callBug() {
        Log.e(TAG, "callBug: ")
//        try {
        var i = 1 / 0
        LogUtils.e("TAG", "i = $i")
        LogUtils.d("TAG", "一行代码不会被插桩，所以加个log增加点代码量~~~~~~")
        LogUtils.d("TAG", "一行代码不会被插桩，所以加个log增加点代码量~~~~~~")
//        } catch (e: Exception) {
//            LogUtils.printError(e)
//        }
    }
}