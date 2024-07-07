package com.techme.jetpack.exoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.techme.jetpack.ext.setBlurImageUrl
import com.techme.jetpack.ext.setImageUrl
import com.techme.jetpack.ext.setVisibility
import com.techme.jetpack.utils.PixUtil
import com.techme.jetpack_android_online.databinding.LayoutListWrapperPlayerViewBinding
import org.w3c.dom.Attr
import kotlin.math.max

/**
 * 动态挂载 视频播放控制器和 显示画面的PlayView
 */
@SuppressLint("ViewConstructor")
class WrapperPlayer constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attr) {
    private val viewBinding = LayoutListWrapperPlayerViewBinding.inflate(LayoutInflater.from(context), this)

    //绑定视频的布局  宽高 url 和视频url 还有最大高度
    fun bindData(
        widthPx: Int,
        heightPx: Int,
        coverUrl: String?,
        videoUrl: String?,
        maxHeight: Int
    ) {
        //根据视频的宽高 widthPx heightPx 动态计算封面view的宽高 还有WrapperView的宽高
        viewBinding.cover.setImageUrl(coverUrl)
        //高斯模糊的背景
        // 如果宽度小于高度，则设置模糊背景的URL和显示状态
        if (widthPx < heightPx) {
            coverUrl?.run {
                // 设置模糊背景的URL
                viewBinding.blurBackground.setBlurImageUrl(this, 10)
                // 设置模糊背景的显示状态
                viewBinding.blurBackground.setVisibility(true)
            }
        } else {
            // 设置模糊背景的显示状态
            viewBinding.blurBackground.setVisibility(false)
        }
        // 设置图片的大小
        setSize(widthPx, heightPx, PixUtil.getScreenWidth(), maxHeight)
    }

    private fun setSize(widthPx: Int, heightPx: Int, maxWidth: Int, maxHeight: Int) {
        //计算视频原始宽度 》原始高度的等比缩放 或者原始高度>原始宽度的等比缩放 cover，wrapperView
        val coverHeight: Int
        // 封面高度
        val coverWidth: Int
        // 封面宽度
        if (widthPx >= heightPx) {
            coverWidth = maxWidth
            // 计算封面高度
            coverHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt()
        } else {
            coverHeight = maxHeight
            // 计算封面宽度
            coverWidth = (widthPx / (heightPx * 1.0f / maxHeight)).toInt()

        }
        //设置wrapperView的宽高
        val wrapperViewParams = layoutParams

        wrapperViewParams.width = maxWidth
        wrapperViewParams.height = coverHeight
        layoutParams = wrapperViewParams
        //设置高斯模糊背景View的宽高
        val blurParams = viewBinding.blurBackground.layoutParams

        //设置模糊背景的宽度为最大宽度
        blurParams.width = maxWidth

        //设置模糊背景的高度为封面高度
        blurParams.height = coverHeight
        viewBinding.blurBackground.layoutParams=blurParams
        //设置封面图（cover-view）的宽高
        //获取cover的布局参数
       val coverParams:LayoutParams =viewBinding.cover.layoutParams as LayoutParams

        //设置cover的宽度
        coverParams.width = coverWidth

        //设置cover的高度
        coverParams.height = coverHeight
        //设置cover的居中显示
        coverParams.gravity=Gravity.CENTER
        //设置cover的缩放类型为适应中心
        viewBinding.cover.scaleType=ImageView.ScaleType.FIT_CENTER
        //设置cover的布局参数
        viewBinding.cover.layoutParams=coverParams


    }
}