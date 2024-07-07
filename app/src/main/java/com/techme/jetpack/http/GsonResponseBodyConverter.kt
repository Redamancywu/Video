package com.techme.jetpack.http

import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonResponseBodyConverter<T> constructor(private val gson: Gson, private val type: Type) :
    Converter<ResponseBody, ApiResult<T>> {
    override fun convert(value: ResponseBody): ApiResult<T>? {
        // 使用ResponseBody
        value.use {
            // 判断返回类型是否为ApiResult<*>
            if (type !is ParameterizedType || !ApiResult::class.java.isAssignableFrom((type.rawType) as Class<*>))
                throw java.lang.RuntimeException("The return type of the method must be ApiResult<*>")
            // 创建ApiResult实例
            val apiResult = ApiResult<T>()
            // 将返回的JSON字符串转换为JSONObject
            val response = JSONObject(value.string())
            // 获取状态码
            apiResult.status = response.optInt("status")
            // 获取错误信息
            apiResult.errMsg = response.optString("message")
            // 获取数据
            val data1: JSONObject? = response.optJSONObject("data")
            // 如果数据不为空
            if (data1 != null) {
                // 获取数据中的数据
                val data2: String? = data1.optString("data")
                // 如果数据不为空
                if (data2 != null) {
                    // 获取类型参数
                    val argumentType = type.actualTypeArguments[0]
                    // 将数据转换为指定类型
                    kotlin.runCatching {
                        apiResult.body = gson.fromJson(data2, argumentType)
                    }.onFailure {
                        // 打印堆栈信息
                        it.printStackTrace()
                    }
                }
            }
            // 返回ApiResult实例
            return apiResult
        }
    }
}