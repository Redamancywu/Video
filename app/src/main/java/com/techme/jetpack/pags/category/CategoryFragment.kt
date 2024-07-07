package com.techme.jetpack.pags.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techme.jetpack.navigation.BaseFragment
import com.techme.jetpack.plugin.runtime.NavDestination
import com.techme.jetpack_android_online.databinding.LayoutFragmentCategoryBinding

@NavDestination(type = NavDestination.NavType.Fragment, route = "category_fragment")
class CategoryFragment : BaseFragment() {

    private lateinit var categoryBinding: LayoutFragmentCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: $savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        categoryBinding = LayoutFragmentCategoryBinding.inflate(inflater, container, false)
        return categoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("outState", "我是categoryFragment")
    }
}