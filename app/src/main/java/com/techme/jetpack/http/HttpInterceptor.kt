package com.techme.jetpack.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class HttpInterceptor : Interceptor {
    private val TAG = "HttpInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取请求
        val request = chain.request()

        // 执行请求
        val response = chain.proceed(request)

        // 打印请求信息
        Log.d(TAG, "<------------------------------------- REQUEST ------------------------------------->")
        Log.d(TAG, "URL: ${request.url}")
        Log.d(TAG, "Method: ${request.method}")
        Log.d(TAG, "Headers: ${request.headers}")
        Log.d(TAG, "Body: ${request.body}")

        // 读取响应体的内容
        val responseBodyString = response.body?.string()

        // 打印响应信息
        Log.d(TAG, "<------------------------------------- RESPONSE ------------------------------------->")
        Log.d(TAG, "Code: ${response.code}")
        Log.d(TAG, "Message: ${response.message}")
        Log.d(TAG, "Headers: ${response.headers}")
        Log.d(TAG, "Body: $responseBodyString")

        // 构建一个新的响应，将响应体内容重新写入
        val newResponseBody = responseBodyString?.toResponseBody(response.body?.contentType())
        return response.newBuilder().body(newResponseBody).build()
    }
}
