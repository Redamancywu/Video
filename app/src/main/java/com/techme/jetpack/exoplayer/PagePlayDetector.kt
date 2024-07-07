package com.techme.jetpack.exoplayer

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
    private val mDetectorListeners:MutableList<IPlayDetector> = arrayListOf()
    private val pageListPlayer=PageListPlayer.get(pageName)

    // 添加播放侦听器
    fun addDetectorListener(listener: IPlayDetector) {
        mDetectorListeners.add(listener)
    }
    // 移除播放侦听器
    fun removeDetectorListener(listener: IPlayDetector) {
        mDetectorListeners.remove(listener)
    }

    // 滚动侦听器
    val scrollListeners:RecyclerView.OnScrollListener=object :RecyclerView.OnScrollListener(){
        // 滚动状态改变
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState==RecyclerView.SCROLL_STATE_IDLE){
                // 空闲状态调用自动播放
                autoPlay()
            }
        }

        // 滚动
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dx==0&&dy==0){
                // 意味着列表初始数据加载成功,当调用了notifyItemRangeInsert之后 item并没有立即添加到列表上
                // 等itemView被正真布局到RecycleView之后会出发onScrolled
                postAutoPlay()
            }else{
                // 滑动中需要检查，正在播放的item，是否滑动出屏幕 如果滑动出了 就停止它
            }
        }
    }
  // 自动播放任务
    private val delayAutoPlayRunnable= Runnable { autoPlay() }
    // 延迟执行自动播放任务
    private fun postAutoPlay() {
       listView.post (delayAutoPlayRunnable)
    }

    private fun autoPlay() {
      if (mDetectorListeners.size<=0||listView.childCount<=0){
          return
      }
        //检查是否有正在播放的item，并且还在屏幕内
        //判断是否在播放中，并且是否占据页面的2/1
        if (pageListPlayer.isPlaying&& isTargetInBounds(pageListPlayer))
    }


    // 声明一个接口IPlayDetector，用于定义一个播放检测器
    interface IPlayDetector {
        // 定义一个方法，用于获取挂载的视图
        fun getAttachView():WrapperPlayer
        // 定义一个方法，用于获取视频的URL
        fun getVideoUrl():String
    }
}