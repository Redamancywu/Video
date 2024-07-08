package com.techme.jetpack.exoplayer

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * 列表滑动后自动播放机制
 */
class PagePlayDetector(
    private val pageName: String,
    private val lifecycleOwner: LifecycleOwner,
    private val listView: RecyclerView
) {
    // 添加播放侦听器
    private val mDetectorListeners: MutableList<IPlayDetector> = arrayListOf()
    private val pageListPlayer = PageListPlayer.get(pageName)

    val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dx == 0 && dy == 0) {
                // 意味着列表初始数据加载成功,当调用了notifyItemRangeInsert之后，item 并没有立即被添加到列表上
                // 等itemView 被真正布局到recyclerview之后，会触发onScrolled
                postAutoPlay()
            } else {
                // 滑动中需要检测，正在播放的item 是否已经滑出屏幕，如果滑出则停止它
                if (pageListPlayer.isPlaying && !isTargetInBounds(pageListPlayer.attachedView)) {
                    pageListPlayer.inActive()
                }
            }
        }
    }
    init {
        listView.addOnScrollListener(scrollListener)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> pageListPlayer.inActive()

                    Lifecycle.Event.ON_RESUME -> pageListPlayer.onActive()

                    Lifecycle.Event.ON_DESTROY -> {

                        mDetectorListeners.clear()
                        listView.removeOnScrollListener(scrollListener)
                        listView.removeCallbacks(delayAutoPlayRunnable)
                        pageListPlayer.stop(false)
                    }

                    else -> {}
                }
            }

        })
    }

    // 添加播放侦听器
    fun addDetectorListener(listener: IPlayDetector) {
        mDetectorListeners.add(listener)
    }

    // 移除播放侦听器
    fun removeDetectorListener(listener: IPlayDetector) {
        mDetectorListeners.remove(listener)
    }

    // 自动播放任务
    private val delayAutoPlayRunnable = Runnable { autoPlay() }

    // 延迟执行自动播放任务
    private fun postAutoPlay() {
        listView.post(delayAutoPlayRunnable)
    }


    private fun autoPlay() {
        if (mDetectorListeners.size <= 0 || listView.childCount <= 0) {
            return
        }
        //检查是否有正在播放的item，并且还在屏幕内
        //判断是否在播放中，并且是否占据页面的2/1
        if (pageListPlayer.isPlaying && isTargetInBounds(pageListPlayer.attachedView)) {
            return
        }
        var attachedViewListener: IPlayDetector? = null
        for (listener in mDetectorListeners) {
            val inBounds = isTargetInBounds(listener.getAttachView())
            if (inBounds) {
                attachedViewListener = listener
                break
            }
        }
        attachedViewListener?.run {
            togglePlay(this.getAttachView(), this.getVideoUrl())
        }
    }

     fun togglePlay(attachView: WrapperPlayer, videoUrl: String) {
        pageListPlayer.togglePlay(attachView, videoUrl)
    }

    // 判断目标视图的2/1是否在屏幕内
    private fun isTargetInBounds(attachedView: ViewGroup?): Boolean {
        // 如果目标视图不存在，则返回false
        if (attachedView == null) {
            return false
        }
        // 如果目标视图没有显示或者没有绑定到窗口，则返回false
        if (attachedView.isShown || !attachedView.isAttachedToWindow) {

            return false
        }
        // 获取目标视图在屏幕上的位置
        val location = IntArray(2)
        attachedView.getLocationOnScreen(location)
        // 计算目标视图的中心位置
        val center = location[1] + attachedView.height / 2
        // 确保RecycleView的局部变量存在
        ensureRecycleViewLocal()
        // 判断目标视图的中心位置是否在RecycleView的范围内
        return rvLocation?.run {
            center in first..second
        } ?: false

    }

    private var rvLocation: Pair<Int, Int>? = null

    // 确保RecycleView的局部变量存在
    private fun ensureRecycleViewLocal() {
        // 如果局部变量不存在，则获取RecycleView在屏幕上的位置
        if (rvLocation == null) {
            val location = IntArray(2)
            listView.getLocationOnScreen(location)
            rvLocation = Pair(location[1], location[1] + listView.height)

        }
    }


    // 声明一个接口IPlayDetector，用于定义一个播放检测器
    interface IPlayDetector {
        // 定义一个方法，用于获取挂载的视图
        fun getAttachView(): WrapperPlayer

        // 定义一个方法，用于获取视频的URL
        fun getVideoUrl(): String
    }
}