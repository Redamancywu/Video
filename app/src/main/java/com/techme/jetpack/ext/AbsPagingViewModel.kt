package com.techme.jetpack.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.techme.jetpack.http.ApiResult

abstract class AbsPagingViewModel<T : Any> : ViewModel() {
    val pageFlow = Pager(config = PagingConfig(
        pageSize = 10, initialLoadSize = 10, enablePlaceholders = false, prefetchDistance = 1
    ), pagingSourceFactory = {
        AbsPagingSource()
    }).flow.cachedIn(viewModelScope)

    inner class AbsPagingSource : PagingSource<Long, T>() {
        override fun getRefreshKey(state: PagingState<Long, T>): Long? {
            return null
        }

        override suspend fun load(params: LoadParams<Long>): LoadResult<Long, T> {
            kotlin.runCatching {
                this@AbsPagingViewModel.doLoadPage(params)
            }.onSuccess {
                if (it.body?.isNotEmpty() == true) {
                    return LoadResult.Page(it.body!!, null, it.nextPageKey)
                }
            }.onFailure {
                it.printStackTrace()
            }
            return if (params.key == null) LoadResult.Page(
                arrayListOf(), null, null
            ) else LoadResult.Error(java.lang.RuntimeException("No more data to fetch"))
        }

        override val keyReuseSupported: Boolean
            get() = true
    }

    abstract suspend fun doLoadPage(params: PagingSource.LoadParams<Long>): ApiResult<List<T>>
}