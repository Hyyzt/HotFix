package com.feelschaotic.upload

import com.alibaba.fastjson.JSON
import com.aliyun.oss.ClientException
import com.aliyun.oss.OSSClient
import com.aliyun.oss.ServiceException
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeUnit

class UploadService {
    OkHttpClient client

    UploadService() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(log)
                .build()
    }

    def updatePatchInfo(Config config, Patch patchInfo, String fileName, OnResponseListener<String> listener) {
        try {
            def requestJson = JSON.toJSONString(patchInfo)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("patchFile", fileName,
                            RequestBody.create(MediaType.parse("multipart/form-data"), new File(fileName)))
                    .addFormDataPart("version", String.valueOf(patchInfo.version))
                    .addFormDataPart("fromSystem", String.valueOf(patchInfo.fromSystem))
                    .addFormDataPart("toDate", String.valueOf(patchInfo.toDate))
                    .addFormDataPart("fromDate", String.valueOf(patchInfo.fromDate))
                    .addFormDataPart("size", String.valueOf(new File(fileName).size()))
                    .addFormDataPart("pKey", String.valueOf(patchInfo.pKey))
                    .addFormDataPart("bundledescribe", String.valueOf(patchInfo.bundledescribe))
                    .addFormDataPart("bundlename", String.valueOf(patchInfo.bundlename))
                    .addFormDataPart("packagename", String.valueOf(patchInfo.packagename))
                    .addFormDataPart("platform", "android")
                    .addFormDataPart("bundleurl", String.valueOf(patchInfo.bundleurl))
                    .build()
            def request = new Request.Builder()
                    .url(config.serverUrl)
                    .header("X-Bmob-Application-Id", config.applicationId)
                    .header("X-Bmob-REST-API-Key", config.restApiKey)
                    .post(requestBody).build()
            def response = client.newCall(request).execute()
            if (listener != null) {
                listener.onSuccess(response.body().string())
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e)
            }
        }
    }
}

interface OnResponseListener<T> {
    def onSuccess(T response)

    def onError(Exception e)
}