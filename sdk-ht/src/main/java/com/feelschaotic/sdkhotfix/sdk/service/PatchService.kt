package com.feelschaotic.sdkhotfix.sdk.service

import android.util.Log
import com.feelschaotic.sdkhotfix.sdk.entity.DownloadRequest
import okhttp3.Headers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * service层，用于和服务端交互，纯网络请求
 * @author feelschaotic
 * @create 2019/6/5.
 */
class PatchService {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(RetryInterceptor())
        .build()

    /**
     * 从远端获取补丁列表
     */
    fun checkVersion(
        url: String,
        headers: Headers,
        map: MutableMap<String, String>,
        listener: RespondListener<String>
    ) {
//        if (map.isEmpty()) {
//            return
//        }
//        val requestJson = JSON.toJSONString(map)
//        client.newCall(Request.Builder()
//            .url(url)
//            .headers(headers)
//            .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestJson))
//            .build())
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response?) {
//                    response?.body()?.let {
//                        listener.onSuccess(it.string())
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException?) {
//                    e?.let {
//                        listener.onError(it)
//                    }
//                }
//            })
        val testResponse =
            "{\"result\": {\"data\": [{\"bundledescribe\": \"修复了/0崩溃异常补丁\",\"bundlename\": \"com.eulertu.opensdk_MY-SAMPLES-SDK_0.0.1\",\"bundleurl\": \"1646796257192-android\",\"fromDate\": 1520904373,\"fromSystem\": \"8.0\",\"pKey\": \"L/C+4tfYYwoEoX6Pf1bLJoJmliZ3It2nGHt/VLHbAWwE33LJsnMEyCJdHr3qUMllbi7ZoW+h/1lC31XmC1OjRasuAuY6qGLPntkNl4EllL9yfXv/RctDi7rUuOZkBF+rTAYVz+f42QDXwcJ8MbrJkrWhnHPzMZIqx0abDoD6Nj4=\",\"packagename\": \"com.eulertu.opensdk_MY-OPEN-SDK_0.0.1\",\"platform\": \"android\",\"size\": 256,\"toDate\": 1585953200,\"version\": \"0.0.1\"}]}}"
        listener.onSuccess(testResponse)
    }

    /**
     * 下载补丁
     */
    fun downloadPatch(request: DownloadRequest, listener: RespondListener<String>) {
        //FileDownloader.downloadSync(request, listener)
        val patchPath = "sdcard/hotfix/patch.jar"
        Log.e(TAG, "downloadPatch: $patchPath")
        listener.onSuccess(patchPath)
    }

    companion object {
        private const val TAG = "PatchService"
    }
}