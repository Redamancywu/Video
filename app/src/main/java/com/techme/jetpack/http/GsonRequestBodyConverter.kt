package com.techme.jetpack.http

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset

internal class GsonRequestBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) :
    Converter<T, RequestBody> {
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        // 创建一个Buffer对象
        val buffer = Buffer()
        // 创建一个OutputStreamWriter对象，将Buffer的outputStream()作为参数传入，并指定编码为UTF-8
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        // 创建一个JsonWriter对象，将writer作为参数传入
        val jsonWriter = gson.newJsonWriter(writer)
        // 使用adapter将value写入jsonWriter
        adapter.write(jsonWriter, value)
        // 关闭jsonWriter
        jsonWriter.close()
        // 返回一个RequestBody对象，其内容为buffer的readByteString()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {
        // 定义一个MediaType对象，内容为"application/json; charset=UTF-8"
        private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
        // 定义一个Charset对象，内容为UTF-8
        private val UTF_8 = Charset.forName("UTF-8")
    }
}