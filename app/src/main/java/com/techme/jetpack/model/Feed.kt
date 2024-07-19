package com.techme.jetpack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 帖子数据类型
const val TYPE_TEXT = 0 //文本类型帖子
const val TYPE_IMAGE_TEXT = 1 //图文类型帖子
const val TYPE_VIDEO = 2//视频类型帖子
data class Feed(
    val activityIcon: String?,
    val activityText: String?,
    val author: Author?,
    val authorId: Long,
    val cover: String?,
    val createTime: Long,
    val duration: Double,
    val feedsText: String?,
    val height: Int,
    val id: Long,
    val itemId: Long,
    val itemType: Int,
    val topComment: TopComment?,
    var ugc: Ugc?,
    val url: String?,
    val width: Int,
    var backgroundColor: Int = 0
){
    fun getUgcOrDefault():Ugc{
        if (ugc==null){
            ugc= Ugc()
        }
        return ugc!!
    }
}
@Entity(tableName = "author")  //room 表
data class Author(
    val avatar: String,
    val commentCount: Int,
    val description: String?,
    val expiresTime: Long,
    val favoriteCount: Int,
    val feedCount: Int,
    val followCount: Int,
    val followerCount: Int,
    val hasFollow: Boolean,
    val historyCount: Int,
    val likeCount: Int,
    val name: String,
    val qqOpenId: String,
    val score: Int,
    @ColumnInfo(name = "topCount", defaultValue = "0")
    val topCount: Int,
    @PrimaryKey(autoGenerate = false)
    val userId: Long
) {
    constructor() : this("", 0, "", 0, 0, 0, 0, 0, false, 0, 0, "", "", 0, 0, 0)
}

data class TopComment(
    val author: Author?,
   //  val commentCount: Int,
    val commentId: Long,
    val commentText: String?,
    val commentType: Int,
    var commentUgc: Ugc?,
    val createTime: Long,
  //  val hasLiked: Boolean,
    val height: Int,
    val id: Int,
    val imageUrl: String?,
    val itemId: Long,
  //   val likeCount: Int,
    val userId: Long,
    val videoUrl: String?,
    val width: Int
){
    fun getUgcOrDefault(): Ugc {
        if (commentUgc == null) {
            commentUgc = Ugc()
        }
        return commentUgc!!
    }
}

data class Ugc(
    val commentCount: Int,
    val hasFavorite: Boolean,
    var hasLiked: Boolean,
    var hasdiss: Boolean,
    val itemId: Long,
    var likeCount: Int,
    val shareCount: Int
) {
    constructor() : this(0, false, false, false, 0, 0, 0)
}

data class CommentUgc(
    val commentId: Long,
    val hasLiked: Boolean,
    val id: Int,
    val likeCount: Int
)