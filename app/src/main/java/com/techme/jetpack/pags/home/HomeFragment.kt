package com.techme.jetpack.pags.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.techme.jetpack.adapter.AbsListFragment
import com.techme.jetpack.ext.invokeViewModel
import com.techme.jetpack.navigation.BaseFragment
import com.techme.jetpack.plugin.runtime.NavDestination
import com.techme.jetpack_android_online.databinding.LayoutFragmentHomeBinding
import kotlinx.coroutines.launch

@NavDestination(type = NavDestination.NavType.Fragment, route = "home_fragment", asStarter = true)
class HomeFragment : AbsListFragment() {
    private val viewModel: HomeViewModel by invokeViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.hotFeeds.collect{
                // 更新列表数据
                submitData(it)
            }
        }
    }
}