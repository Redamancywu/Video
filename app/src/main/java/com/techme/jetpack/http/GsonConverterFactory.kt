package com.techme.jetpack.http

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class GsonConverterFactory : Converter.Factory() {
    // 声明一个Gson对象
    private val gson = Gson()
    // 重写responseBodyConverter方法，用于将ResponseBody转换为指定类型
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        // 返回一个GsonResponseBodyConverter对象，用于将ResponseBody转换为指定类型
        return GsonResponseBodyConverter<Any>(gson,type)
    }

    // 重写requestBodyConverter方法，用于创建请求体转换器
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        // 获取类型适配器
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        // 返回GsonRequestBodyConverter
        return GsonRequestBodyConverter(gson, adapter)
    }
}