package com.techme.jetpack.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.techme.jetpack_android_online.databinding.LayoutLoadingStatusViewBinding


/**
 * 页面加载通用状态View
 */
class LoadingStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding =
        LayoutLoadingStatusViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.loading.show()
    }

    @SuppressLint("ResourceType")
    fun showEmpty(
        @DrawableRes iconRes: Int = 0,
        text: String? = null,
        retry: OnClickListener? = null
    ) {
        binding.loading.hide()
        binding.emptyLayout.visibility = View.VISIBLE
        if (iconRes > 0) {
            binding.emptyIcon.setImageResource(iconRes)
        }
        if (!TextUtils.isEmpty(text)) {
            binding.emptyText.text = text
            binding.emptyText.visibility = View.VISIBLE
        }

        retry?.let {
            binding.emptyAction.visibility = View.VISIBLE
            binding.emptyAction.setOnClickListener(it)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility != View.VISIBLE) {
            binding.loading.hide()
        }
    }
}
