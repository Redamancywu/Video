package com.techme.jetpack.utils

import android.app.Application

private var sApplication: Application? = null

/**
 * 获取当前应用程序的Application实例
 */
object AppGlobals {
    // 获取Application
    fun getApplication(): Application {
        // 如果sApplication为空
        if (sApplication == null) {
            // 运行try-catch块
            kotlin.runCatching {
                // 获取ActivityThread的currentApplication方法
                sApplication =  Class.forName("android.app.ActivityThread").getMethod("currentApplication")
                    // 调用currentApplication方法，获取Application
                    .invoke(null, *emptyArray()) as Application
            }.onFailure {
                // 打印堆栈跟踪
                it.printStackTrace()
            }
        }
        // 返回sApplication
        return sApplication!!
    }
}