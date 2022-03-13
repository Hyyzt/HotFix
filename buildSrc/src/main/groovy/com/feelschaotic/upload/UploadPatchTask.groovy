package com.feelschaotic.upload

import com.alibaba.fastjson.JSONObject
import com.feelschaotic.crypto.crypto.EncryptManager
import com.feelschaotic.upload.utils.EncryptUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UploadPatchTask extends DefaultTask {
    def rootDir = project.rootDir.path
    def patchName = "patch.jar"
    def patchPath

    def byteUtil = new com.feelschaotic.upload.utils.ByteUtil()
    def uploadService = new UploadService()

    def config

    @TaskAction
    def upload() {
        init()
        checkPatch()
    }

    def init() {
        /*发布脚本和测试脚本的唯一区别：常量配置的取值不同*/
        def buildConfigUtil = new com.feelschaotic.upload.utils.BuildConfigUtil(project)
        config = new Config()
        config.serverUrl = buildConfigUtil.getReleaseBuildType("HOTFIX_SERVER_URL")
        config.applicationId = buildConfigUtil.getReleaseBuildType("BMOB_APPLICATION_ID")
        config.restApiKey = buildConfigUtil.getReleaseBuildType("REST_API_KEY")

        patchPath = rootDir + File.separator + patchName
    }

    def checkPatch() {
        def file = new File(patchPath)
        if (!file.exists()) {
            println '--【警告】本地补丁文件不存在:' + patchPath
            return
        }
        encryptPatch()
    }

    def encryptPatch() {
        //pkey
        def clientAesKey = new EncryptUtil().getRandomString(16)
        println "--clientAesKey: ${clientAesKey}"
        //用pKey加密文件
        byte[] encryptBytes = EncryptManager.getInstance().encryptByAes(byteUtil.fileToBytes(patchPath), clientAesKey.getBytes())
        byteUtil.bytesToFile(encryptBytes, rootDir, patchName)
        //在加密pKey
        def encryptAesKey = EncryptManager.getInstance().encryptByRsaPublicKey(clientAesKey.getBytes())
        uploadPatchToAliYun(new String(Base64.encoder.encode(encryptAesKey)))
    }

    def uploadPatchToAliYun(String encryptKey) {
        def file = new File(patchPath)
        uploadService.updatePatchInfo(config, createPatch(file.getName(), file.size(), encryptKey), file.getAbsolutePath(), new OnResponseListener<String>() {
            @Override
            def onSuccess(String response) {
                println "--【成功】上传补丁成功，返回信息如下："
                println(response)
            }

            @Override
            def onError(Exception e) {
                println "--【警告】上传补丁失败，错误信息如下："
                e.printStackTrace()
            }
        })
    }

    Patch createPatch(String objectName, long fileSize, String encryptKey) {
        def patch = new Patch(project)
        patch.bundleurl = objectName
        patch.size = fileSize
        patch.pKey = encryptKey
        println "--AesKeyBase64: ${encryptKey}"
        return patch
    }
}