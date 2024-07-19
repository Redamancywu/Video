// 包含此组件的软件包名称
package com.techme.jetpack.login.ui

// 导入必要的类和接口
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.techme.jetpack.cache.CacheManger
import com.techme.jetpack.model.Author
import com.techme.jetpack.utils.AppGlobals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

// 单例对象，用于管理用户的登录状态和数据
object UserManager {
    // 使用MutableStateFlow创建一个可变的流，初始值为一个空的Author对象
    private val userFlow: MutableStateFlow<Author> = MutableStateFlow(Author())

    // 挂起函数，用于保存Author对象至数据库，并更新userFlow
    suspend fun save(author: Author) {
        CacheManger.get().authorDao.save(author)
        userFlow.emit(author) // 发射最新的作者信息到流中
    }

    // 判断用户是否登录，通过检查Author对象的过期时间
    fun isLogin(): Boolean {
        return userFlow.value.expiresTime > System.currentTimeMillis()
    }

    // 如果需要，启动登录界面
    fun loginIfNeed() {
        if (!isLogin()) { // 如果用户未登录
            // 创建登录界面的Intent
            val intent = Intent(AppGlobals.getApplication(), LoginActivity::class.java)
            // 设置Intent的标志，确保新任务栈的顶部是此活动
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // 启动登录活动
            AppGlobals.getApplication().startActivity(intent)
        }
    }

    // 返回一个Flow，用于观察Author对象的变化
    suspend fun getUser(): Flow<Author> {
        loadCache() // 确保缓存中加载了最新的用户数据
        return userFlow // 返回可观察的作者信息流
    }

    // 获取当前登录用户的ID
    suspend fun userId(): Long {
        loadCache() // 确保缓存中加载了最新的用户数据
        return userFlow.value.userId // 返回用户ID
    }

    // 私有挂起函数，用于从缓存中加载用户数据
    private suspend fun loadCache() {
        // 首先，检查userFlow的当前值是否已经有效
        if (userFlow.value.expiresTime <= System.currentTimeMillis()) {
            // 尝试从CacheManager获取最新的用户数据
            val cachedAuthor = CacheManger.get().authorDao.getUser()
            // 如果缓存中存在有效的用户数据，更新userFlow
            cachedAuthor?.let {
                if (it.expiresTime > System.currentTimeMillis()) {
                    userFlow.emit(it)
                }
            }
        }
    }
}