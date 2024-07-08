package com.techme.jetpack.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import com.techme.jetpack.model.Feed
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.techme.jetpack.exoplayer.PagePlayDetector
import com.techme.jetpack.exoplayer.WrapperPlayer
import com.techme.jetpack.ext.load
import com.techme.jetpack.ext.setImageResource
import com.techme.jetpack.ext.setImageUrl
import com.techme.jetpack.ext.setMaterialButton
import com.techme.jetpack.ext.setTextVisibility
import com.techme.jetpack.ext.setVisibility
import com.techme.jetpack.model.Author
import com.techme.jetpack.model.TYPE_IMAGE_TEXT
import com.techme.jetpack.model.TYPE_TEXT
import com.techme.jetpack.model.TYPE_VIDEO
import com.techme.jetpack.model.TopComment
import com.techme.jetpack.model.Ugc
import com.techme.jetpack.utils.PixUtil
import com.techme.jetpack_android_online.R
import com.techme.jetpack_android_online.databinding.LayoutFeedAuthorBinding
import com.techme.jetpack_android_online.databinding.LayoutFeedInteractionBinding
import com.techme.jetpack_android_online.databinding.LayoutFeedLabelBinding
import com.techme.jetpack_android_online.databinding.LayoutFeedTextBinding
import com.techme.jetpack_android_online.databinding.LayoutFeedTopCommentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedAdapter(private val pageName: String, private val lifecycleOwner: LifecycleOwner) :
    PagingDataAdapter<Feed, FeedAdapter.FeedViewHolder>(object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            // 判断两个Feed的id是否相同
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            // 判断两个Feed是否相同
            return oldItem == newItem
        }

    }) {


    private lateinit var playDetector: PagePlayDetector

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        PagePlayDetector.IPlayDetector {
        private val authorBinding =
            LayoutFeedAuthorBinding.bind(itemView.findViewById(R.id.feed_author))
        private val feedTextBinding =
            LayoutFeedTextBinding.bind(itemView.findViewById(R.id.feed_text))
        private val feedImage: ImageView? = itemView.findViewById(R.id.feed_image)
        private val labelBinding =
            LayoutFeedLabelBinding.bind(itemView.findViewById(R.id.feed_label))
        private val commentBinding =
            LayoutFeedTopCommentBinding.bind(itemView.findViewById(R.id.feed_comment))
        private val interactionBinding =
            LayoutFeedInteractionBinding.bind(itemView.findViewById(R.id.feed_interaction))
        private val wrapperPlayView =
            itemView.findViewById<WrapperPlayer>(R.id.feed_video)

        fun bindAuthor(author: Author?) {
            authorBinding.authorAvatar.setImageUrl(author?.avatar, true)
            authorBinding.authorName.text = author?.name
        }

        fun bindFeedContent(feedsText: String?) {
            feedTextBinding.root.setTextVisibility(feedsText)
        }

        // 根据传入的宽高和最大高度，加载并设置图片大小
        fun bindFeedImage(width: Int, height: Int, maxheight: Int, cover: String?) {
            // 如果图片为空或者封面为空，则隐藏图片
            if (feedImage == null || TextUtils.isEmpty(cover)) {
                feedImage?.visibility = View.GONE
                return
            }
            val feedItem = getItem(layoutPosition) ?: return
            // 显示图片
            feedImage.visibility = View.VISIBLE
            // 加载图片
            feedImage.load(cover!!) {
                // 如果宽度和高度都大于0，则设置图片大小
                if (width > 0 && height > 0) {
                    setImageSize(width, height, maxheight)
                }
                // 如果宽度和高度都小于0，则设置图片大小为图片的实际宽度和高度
                if (width < 0 && height < 0) {
                    setImageSize(it.width, it.height, maxheight)
                }

                if (feedItem.backgroundColor == 0) {
                    lifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.IO) {   //在子线程
                        //设置调色盘边距颜色
                        val defaultColor = feedImage.context.getColor(R.color.color_3d3)
                        val color = Palette.Builder(it).generate().getMutedColor(defaultColor)
                        feedItem.backgroundColor = color
                        withContext(lifecycleOwner.lifecycle.coroutineScope.coroutineContext) {
                            feedImage.background = ColorDrawable(feedItem.backgroundColor)
                        }
                    }
                }
                feedImage.background = ColorDrawable(feedItem.backgroundColor)

            }

        }

        //设置图片大小
        private fun setImageSize(width: Int, height: Int, maxheight: Int) {
            //获取屏幕宽度
            val finalWidth = PixUtil.getScreenWidth()
            //如果宽度大于高度，则高度为宽度，否则为maxheight
            val finalHeight = if (width > height) {
                (height / (width * 1.0f / finalWidth)).toInt()
            } else {
                maxheight
            }
            //获取图片的布局参数
            val params = feedImage!!.layoutParams as LinearLayout.LayoutParams
            //设置图片宽度为finalWidth
            params.width = finalWidth
            //设置图片高度为finalHeight
            params.height = finalHeight
            //设置图片居中显示
            params.gravity = Gravity.CENTER
            //设置图片填充方式为适应
            feedImage.scaleType = ImageView.ScaleType.FIT_CENTER
            feedImage.layoutParams = params


        }

        fun bindFeedLabel(activityText: String?) {
            labelBinding.root.setTextVisibility(activityText)
        }

        fun bindFeedComment(topComment: TopComment?) {
            // 如果topComment不为空，则显示commentBinding.root
            commentBinding.root.setVisibility(topComment != null)
            // 如果topComment不为空，则显示commentBinding.mediaLayout
            commentBinding.mediaLayout.setVisibility(topComment?.imageUrl != null)

            topComment?.run {
                commentBinding.commentAvatar.setImageUrl(author?.avatar, true)
                // 显示评论者的名字
                commentBinding.commentAuthor.setTextVisibility(author?.name)
                // 显示评论内容
                commentBinding.commentText.setTextVisibility(commentText.toString())
                // 显示点赞数
                commentBinding.commentLikeCount.setTextVisibility(commentText.toString())
                // 显示预览视频播放按钮
                commentBinding.commentPreviewVideoPlay.setVisibility(videoUrl != null)
                //显示点赞数颜色
                commentBinding.commentLikeStatus.setImageResource(
                    topComment.hasLiked,
                    R.drawable.icon_cell_liked,
                    R.drawable.icon_cell_like
                )

            }
        }

        fun bindInteraction(ugc: Ugc?) {
            ugc?.let {
                val context = itemView.context
                // 设置点赞的值
                interactionBinding.interactionLike.text = ugc.likeCount.toString()
                //设置评论的值
                interactionBinding.interactionComment.text = ugc.commentCount.toString()
                //设置分享的值
                interactionBinding.interactionShare.text = ugc.shareCount.toString()
                //设置点赞图标
                interactionBinding.interactionLike.setMaterialButton(
                    ugc.likeCount.toString(),
                    ugc.hasLiked, R.drawable.icon_cell_liked,
                    R.drawable.icon_cell_like
                )
                //设置评论图标
                interactionBinding.interactionDiss.setMaterialButton(
                    null, ugc.hasdiss,
                    R.drawable.icon_cell_dissed,
                    R.drawable.icon_cell_diss
                )
                //设置点赞颜色
                val likeStateColor =
                    ColorStateList.valueOf(context.getColor(if (ugc.hasLiked) R.color.color_theme else R.color.color_3d3))
                interactionBinding.interactionLike.iconTint = likeStateColor
                interactionBinding.interactionLike.setTextColor(likeStateColor)
                //设置评论按钮
                val dissStateColor =
                    ColorStateList.valueOf(context.getColor(if (ugc.hasLiked) R.color.color_theme else R.color.color_3d3))
                interactionBinding.interactionDiss.iconTint = dissStateColor
                interactionBinding.interactionDiss.setTextColor(dissStateColor)

            }
        }

        fun bindFeedVideo(width: Int, height: Int, maxHeight: Int, cover: String?, url: String?) {
            url?.run {
                wrapperPlayView?.run {
                    setVisibility(true)
                    bindData(width, height, cover, url, maxHeight)
                    setListener(object : WrapperPlayer.Listener {
                        override fun onTogglePlay(attachView: WrapperPlayer) {
                            playDetector.togglePlay(attachView, url)
                        }

                    })
                }
            }
        }

        override fun getAttachView(): WrapperPlayer {
            return wrapperPlayView
        }

        override fun getVideoUrl(): String {
            return getItem(layoutPosition)?.url!!
        }

        fun isVideo(): Boolean {
            return getItem(layoutPosition)?.itemType == TYPE_VIDEO
        }

    }

    override fun onViewAttachedToWindow(holder: FeedViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.isVideo()) {
            playDetector.addDetectorListener(holder)
        }

    }

    override fun onViewDetachedFromWindow(holder: FeedViewHolder) {
        super.onViewDetachedFromWindow(holder)
        playDetector.removeDetectorListener(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        playDetector = PagePlayDetector(pageName, lifecycleOwner, recyclerView)
    }


    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = getItem(position) ?: return
        holder.itemView.findViewById<TextView>(R.id.feed_text).text = feed.feedsText
        holder.bindAuthor(feed.author)
        holder.bindFeedContent(feed.feedsText)
        // holder.bindFeedImage(feed.width, feed.height, PixUtil.dp2px(300), feed.cover)
        if (feed.itemType != TYPE_VIDEO) {
            holder.bindFeedImage(feed.width, feed.height, PixUtil.dp2px(300), feed.cover)
        } else {
            holder.bindFeedVideo(feed.width, feed.height, PixUtil.dp2px(300), feed.cover, feed.url)
        }
        holder.bindFeedLabel(feed.activityText)
        holder.bindFeedComment(feed.topComment)
        holder.bindInteraction(feed.ugc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        // 如果视图类型不是文本、图片文本或视频，则创建一个视图，将其可见性设置为GONE，并返回一个FeedViewHolder对象
        if (viewType != TYPE_TEXT && viewType != TYPE_IMAGE_TEXT && viewType != TYPE_VIDEO) {
            val view = View(parent.context)
            view.visibility = View.GONE
            return FeedViewHolder(view)
        }
        val layout =
            if (viewType == TYPE_IMAGE_TEXT || viewType == TYPE_TEXT) R.layout.layout_feed_type_image else R.layout.layout_feed_type_video
        return FeedViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        //// 获取指定位置的item，如果不存在则返回0
        val feedItem = getItem(position) ?: return 0
        return feedItem.itemType

    }
}