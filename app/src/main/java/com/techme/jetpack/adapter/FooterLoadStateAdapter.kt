package com.techme.jetpack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techme.jetpack_android_online.R
import com.techme.jetpack_android_online.databinding.LayoutAbsListLoadingFooterBinding.inflate
import com.techme.jetpack_android_online.databinding.LayoutAbsListLoadingFooterBinding

/**
 * 这段Kotlin代码定义了一个自定义的`RecyclerView.ViewHolder`，用于在`RecyclerView`中显示加载状态。`LoadStateViewHolder`类实现了`RecyclerView.ViewHolder`接口，并重写了`onBindViewHolder`和`onCreateViewHolder`方法。
 *
 * 1. `onBindViewHolder`方法：
 *    - 获取`LoadStateViewHolder`的`binding`对象，该对象包含了`RecyclerView`的视图。
 *    - 根据`loadState`的值，设置不同的加载状态文本。
 *    - 如果`loadState`为`LoadState.Loading`，则显示加载动画，并隐藏加载文本。
 *    - 如果`loadState`为`LoadState.Error`，则设置加载错误的文本。
 *    - 最后，隐藏加载动画。
 *
 * 2. `onCreateViewHolder`方法：
 *    - 使用`LayoutInflater`从`parent.context`中加载布局文件，并返回一个`LoadStateViewHolder`实例。
 *
 * `LoadStateViewHolder`类使用了`ViewBinding`，这是一个Kotlin特有的特性，用于简化UI绑定。通过`LayoutAbsListLoadingFooterBinding`，`LoadStateViewHolder`可以访问`RecyclerView`的视图，并对其进行操作。
 */
class FooterLoadStateAdapter : LoadStateAdapter<FooterLoadStateAdapter.LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        // 获取视图
        val loading = holder.binding.loading
        val loadingText = holder.binding.text
        // 根据loadState判断显示的文本
        when (loadState) {
            is LoadState.Loading -> {
                // 显示加载中
                loadingText.setText(R.string.abs_list_loading_footer_loading)
                loading.show()
                return
            }
            is LoadState.Error -> {
                // 显示错误
                loadingText.setText(R.string.abs_list_loading_footer_error)
            }
            else -> {}
        }
        // 隐藏加载图标
        loading.hide()
        // 延迟隐藏
        loading.postOnAnimation { loading.visibility = View.GONE }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder {
        // 从布局文件中加载视图
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    // 创建一个LoadStateViewHolder类，用于加载状态视图holder
    class LoadStateViewHolder(val binding: LayoutAbsListLoadingFooterBinding) :
        // 继承RecyclerView.ViewHolder类，并传入binding.root作为参数
        RecyclerView.ViewHolder(binding.root)
}