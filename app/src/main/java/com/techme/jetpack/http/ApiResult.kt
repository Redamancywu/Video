package com.techme.jetpack.http

class ApiResult<T> {
    internal var nextPageKey: Long? = 0
    internal var status = 0
    val success
        get() = status == 200
    var errMsg: String = ""
    var body: T? = null
}