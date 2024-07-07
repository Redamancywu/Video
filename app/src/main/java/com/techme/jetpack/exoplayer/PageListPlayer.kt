package com.techme.jetpack.exoplayer

import android.view.ViewGroup

class PageListPlayer:IPlayList {
    override val attachedView: ViewGroup?
        get() = null
    override val isPlaying: Boolean
        get() = false

    override fun inActive() {
    }

    override fun onActive() {
    }

    override fun togglePlay(attachView: WrapperPlayer, videoUrl: String) {
    }

    override fun stop(release: Boolean) {
    }

    //匿名类
    companion object{
        // 存储页面玩家对象的哈希表
        private val mPageListPlayers= hashMapOf<String,IPlayList>()
        // 根据页面名称获取页面玩家对象
        fun get( pageName:String):IPlayList{
            var pageListPlayer= mPageListPlayers[pageName]
            // 如果页面玩家对象为空，则创建一个新的页面玩家对象
            if (pageListPlayer == null){
                pageListPlayer=PageListPlayer()
                mPageListPlayers[pageName]=pageListPlayer
            }
            // 返回页面玩家对象
            return pageListPlayer
        }
        // 停止页面玩家对象
        fun stop(pageName:String,release:Boolean=true){
            // 如果需要释放资源，则从哈希表中移除页面玩家对象并调用stop方法
            if (release){
                mPageListPlayers.remove(pageName)?.stop(true)
            }else{
                // 否则，只调用stop方法
                mPageListPlayers[pageName]?.stop(false)
            }

        }
    }
}