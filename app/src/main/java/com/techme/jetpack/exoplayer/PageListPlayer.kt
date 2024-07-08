package com.techme.jetpack.exoplayer

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider

import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.techme.jetpack.utils.AppGlobals
import com.techme.jetpack_android_online.R

class PageListPlayer : IPlayList, Player.Listener, StyledPlayerControlView.VisibilityListener {
    private var exoPlayer: ExoPlayer
    private var exoPlayView: StyledPlayerView
    private var exoController: StyledPlayerControlView
    private var playingUrl: String? = null
    private var playing: Boolean = false

    override var attachedView: WrapperPlayer? = null

    override val isPlaying: Boolean
        get() = playing

    init {
        val application = AppGlobals.getApplication()
        exoPlayer = ExoPlayer.Builder(application).build()
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayView = LayoutInflater.from(application).inflate(
            R.layout.layout_exo_player_view,
            null
        ) as StyledPlayerView
        exoController = LayoutInflater.from(application).inflate(
            R.layout.layout_exo_player_controller_view,
            null
        ) as StyledPlayerControlView
        //把播放器实例 和playView和controllerView相关联
        //如此视频画面才会正常显示出来 ，播放进度条和时间才能自动更新
        exoPlayView.player = exoPlayer
        exoController.player = exoPlayer

    }

    override fun inActive() {
        if (TextUtils.isEmpty(playingUrl) || attachedView == null) {

            return
        }
        exoPlayer.playWhenReady = false
        exoPlayer.removeListener(this)
        exoController.removeVisibilityListener(this)
        attachedView?.inActive()
    }

    override fun onActive() {
        if (TextUtils.isEmpty(playingUrl) || attachedView == null) {
            return
        }
        exoPlayer.playWhenReady = true
        exoPlayer.addListener(this)
        exoController.addVisibilityListener(this)
        exoController.show()
        attachedView?.onActive(exoPlayView, exoController)
        if (exoPlayer.playbackState == Player.STATE_READY) {
            onPlayerStateChanged(true, Player.STATE_READY)
        } else if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun togglePlay(attachView: WrapperPlayer, videoUrl: String) {
        attachedView?.setOnTouchListener(null)
        attachView.setOnTouchListener { _, _ ->
            exoController.show()
            true
        }


        if (TextUtils.equals(videoUrl, playingUrl)) {
            //意外着是 点击了正在播放的item  暂停或继续播放按钮
            if (playing) {
                inActive()
            } else {
                onActive()
            }
        } else {
            inActive()
            this.playingUrl = videoUrl
            this.attachedView = attachView
            exoPlayer.setMediaSource(createMediaSource(videoUrl))
            exoPlayer.prepare()
            onActive()
        }
    }

    override fun stop(release: Boolean) {
        playing = false
        playingUrl = null
        exoPlayer.playWhenReady = false
        exoController.hideImmediately()
        attachedView?.removeView(exoController)
        attachedView?.removeView(exoPlayView)
        attachedView = null
        if (release) {
            exoPlayer.release()
        }
    }

    override fun onVisibilityChange(visibility: Int) {
        attachedView?.onVisibilityChanged(visibility, exoPlayer.playbackState == Player.STATE_ENDED)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        playing = playbackState == Player.STATE_READY && playWhenReady
        attachedView?.onPlayerStateChanged(isPlaying, playbackState)
        Log.d("PageListPlayer", "onPlayerStateChanged: $playbackState")

    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        exoPlayer.playWhenReady = true
    }

    //匿名类
    companion object {
        // 存储页面玩家对象的哈希表
        private val mPageListPlayers = hashMapOf<String, IPlayList>()

        //构建MediaSource
        private val application = AppGlobals.getApplication()
        private val cache = SimpleCache(
            application.cacheDir,
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 200),
            StandaloneDatabaseProvider(
                application
            )
        )
        private val cacheDataSourceFactor = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setCacheWriteDataSinkFactory(
                CacheDataSink.Factory().setCache(cache).setFragmentSize(Long.MAX_VALUE)
            )
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
        private val progressiveMediaSourceFactory =
            ProgressiveMediaSource.Factory(cacheDataSourceFactor)

        // 根据页面名称获取页面玩家对象
        fun get(pageName: String): IPlayList {
            var pageListPlayer = mPageListPlayers[pageName]
            // 如果页面玩家对象为空，则创建一个新的页面玩家对象
            if (pageListPlayer == null) {
                pageListPlayer = PageListPlayer()
                mPageListPlayers[pageName] = pageListPlayer
            }
            // 返回页面玩家对象
            return pageListPlayer
        }

        // 停止页面玩家对象
        fun stop(pageName: String, release: Boolean = true) {
            // 如果需要释放资源，则从哈希表中移除页面玩家对象并调用stop方法
            if (release) {
                mPageListPlayers.remove(pageName)?.stop(true)
            } else {
                // 否则，只调用stop方法
                mPageListPlayers[pageName]?.stop(false)
            }

        }
    }

    fun createMediaSource(videoUrl: String): MediaSource {
        return progressiveMediaSourceFactory.createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))
    }


}