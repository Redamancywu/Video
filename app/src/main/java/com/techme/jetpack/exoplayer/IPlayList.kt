package com.techme.jetpack.exoplayer

import android.view.ViewGroup

interface IPlayList {
    /**
     * 获取当前视频播放器的exoPlayer（TextureView）是否已被挂在到某个item的容器上
     */
    val attachedView: ViewGroup?

    // 是否正在播放
    val isPlaying: Boolean

    // 页面不可见时 暂停播放
    fun inActive()

    // 页面可见时 恢复播放
    fun onActive()

    // 切换播放状态 点击暂停或回复按钮
    fun togglePlay(attachView: WrapperPlayer, videoUrl: String)

    //释放视频播放器资源
    fun stop(release:Boolean)


}