package com.techme.jetpack.pags.home

import com.techme.jetpack.model.Feed
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.techme.jetpack.http.ApiResult
import com.techme.jetpack.http.ApiService

class HomeViewModel : ViewModel() {
    val hotFeeds = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            enablePlaceholders = false
        ), pagingSourceFactory = {
            HomePageSource()
        }).flow.cachedIn(viewModelScope)
    private var feedType: String = "all"
    fun setFeedType(feedType: String) {
        this.feedType = feedType
    }

    //实现pagingSource数据源  第一个参数是类型 第二个参数是数据 Feed
    inner class HomePageSource : PagingSource<Long, Feed>() {
        override fun getRefreshKey(state: PagingState<Long, Feed>): Long? {
            //第一次加载的时候拿到分页参数
            return null
        }

        override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Feed> {
            val result = kotlin.runCatching {
                /**
                 * 这段代码是Kotlin语言编写的，用于从某个服务（ApiService）获取数据。具体来说，它执行以下操作：
                 *
                 * 1. 使用`kotlin.runCatching`函数包装`ApiService.getService().getFeeds(feedId = params.key?:0L)`这段代码。`runCatching`函数用于捕获异常，以便在发生错误时进行处理。
                 *
                 * 2. 调用`ApiService.getService()`获取服务对象。
                 *
                 * 3. 调用`getFeeds`方法，传入`feedId`参数。如果`params.key`为空，则使用默认值0。
                 *
                 * 总之，这段代码的作用是从某个服务（ApiService）获取数据，并在发生错误时进行处理。
                 */
                ApiService.getService()
                    .getFeeds(feedId = params.key ?: 0L, feedType = feedType)  //0L是为第一页数据也就是初始数据
            }
            val apiResult = result.getOrDefault(ApiResult())
            if (apiResult.success && apiResult.body?.isNotEmpty() == true) {
                return LoadResult.Page(apiResult.body!!, null, apiResult.body!!.last().id)
                //page里面三个参数
                //第一个参数是加载的数据
                //第二个参数prevKey向上加载
                //第三个参数是nextKey向下加载
            }
            //当加载初始数据失败的时候
            return if (params.key == null) LoadResult.Page(
                arrayListOf(),
                null,
                0
            ) else LoadResult.Error(RuntimeException("加载失败"))

        }

    }

}