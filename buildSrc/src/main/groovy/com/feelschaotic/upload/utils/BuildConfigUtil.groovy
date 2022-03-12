package com.feelschaotic.upload.utils

import org.gradle.api.Project

/**
 * BuildConfig工具类
 * 用于帮助gradle脚本获取buildTypes配置
 */
class BuildConfigUtil {
    Project project

    BuildConfigUtil(Project project) {
        this.project = project
    }

    String getDebugBuildType(String key) {
        for (def type : project.android.buildTypes) {
            if (type.debuggable == true) {
                String result = type.getBuildConfigFields()[key].getProperties().get("value")
                return result.replace("\"", "")
            }
        }
        return ""
    }

    String getReleaseBuildType(String key) {
        for (def type : project.android.buildTypes) {
            if (type.debuggable == false) {
                String result = type.getBuildConfigFields()[key].getProperties().get("value")
                print(result)
                return result.replace("\"", "")
            }
        }
        return ""
    }

}