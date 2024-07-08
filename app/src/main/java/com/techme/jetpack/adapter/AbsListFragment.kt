package com.techme.jetpack.adapter

import com.techme.jetpack.model.Feed
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.techme.jetpack.ext.invokeViewBinding
import com.techme.jetpack.ext.setVisibility
import com.techme.jetpack_android_online.R
import com.techme.jetpack_android_online.databinding.LayoutAbsListFragmentBinding
import kotlinx.coroutines.launch

open class AbsListFragment : Fragment(R.layout.layout_abs_list_fragment) {
    private val viewBinding: LayoutAbsListFragmentBinding by invokeViewBinding()  //属性代理自动获取viewbinding
    private lateinit var feedAdapter: FeedAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        // 获取上下文
        val context: Context = requireContext()
        // 设置列表的布局管理器
        viewBinding.listView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // 添加分隔符
        viewBinding.listView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        // 创建适配器
        feedAdapter = FeedAdapter(getFeedType(), lifecycleOwner = viewLifecycleOwner)
        // 添加加载状态footer
        val contactAdapter = feedAdapter.withLoadStateFooter(FooterLoadStateAdapter())
        // 设置适配器
        viewBinding.listView.adapter = contactAdapter
        // 设置颜色
        viewBinding.refreshLayout.setColorSchemeColors(context.getColor(R.color.color_theme))
        //设置刷新监听
        viewBinding.refreshLayout.setOnRefreshListener {
            feedAdapter.refresh()
        }
        // 监听列表数据加载状态，刷新，重试
        //当列表数据加载状态发生变化时，更新列表视图的显示状态  如果没有数据就加载兜底页
        // 启动一个新的生命周期范围，并在该范围内执行提供的lambda表达式
        lifecycleScope.launch {
            // 收集onPagesUpdatedFlow流中的数据
            feedAdapter.onPagesUpdatedFlow.collect {
                // 如果数据不为空，则显示列表视图，否则隐藏列表视图
                val hasData = feedAdapter.itemCount > 0
                viewBinding.refreshLayout.isRefreshing = false
                viewBinding.listView.setVisibility(hasData)
                // 如果数据为空，则显示加载状态，否则隐藏加载状态
                viewBinding.loadingStatus.setVisibility(!hasData)
                if (!hasData) {
                    viewBinding.loadingStatus.showEmpty {
                        feedAdapter.retry()
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun getFeedType(): String {
        return arguments?.getString("feedType") ?: "all"
    }

    fun submitData(pagingData: PagingData<Feed>) {
        lifecycleScope.launch {
            feedAdapter.submitData(pagingData)
        }
    }


}