package com.techme.jetpack.pags.tags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.techme.jetpack.http.ApiService
import com.techme.jetpack.navigation.BaseFragment
import com.techme.jetpack.navigation.navigateBack
import com.techme.jetpack.plugin.runtime.NavDestination
import com.techme.jetpack_android_online.databinding.LayoutFragmentTagsBinding

import kotlinx.coroutines.launch

@NavDestination(type = NavDestination.NavType.Fragment, route = "tags_fragment")
class TagsFragment : BaseFragment() {
    lateinit var tagsBinding: LayoutFragmentTagsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            ApiService.getService().toggleDissFeed(111,222)
        }
        Log.e("fragmentlife", "TagsFragment onCreate:$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tagsBinding = LayoutFragmentTagsBinding.inflate(inflater, container, false);
        return tagsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //homeFragment--categoryFragment---tagsFragment------userFragment
        //---------------------------------------------NavOptions
        tagsBinding.navigateToUserFragment.setOnClickListener {
            findNavController().navigateBack("home_fragment", inclusive = false, saveState = true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("outState", "我是TagsFragment")
    }
}