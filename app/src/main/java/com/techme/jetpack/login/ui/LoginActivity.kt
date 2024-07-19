// 定义包名和导入必要的类和库
package com.techme.jetpack.login.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.techme.jetpack.ext.invokeViewBinding
import com.techme.jetpack.http.ApiService
import com.techme.jetpack_android_online.R
import com.techme.jetpack_android_online.databinding.ActivityLoginBinding
import com.tencent.connect.UserInfo
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// 定义一个LoginActivity类，继承自AppCompatActivity
class LoginActivity : AppCompatActivity() {
    // 声明Tencent实例变量
    private lateinit var tencent: Tencent

    // 使用invokeViewBinding来自动绑定布局视图
    private val viewBinding: ActivityLoginBinding by invokeViewBinding()

    // 在onCreate方法中设置布局并初始化组件
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root) // 设置布局
        // 设置关闭按钮的点击事件
        viewBinding.actionClose.setOnClickListener { finish() }
        // 设置QQ登录按钮的点击事件
        viewBinding.loginWithQq.setOnClickListener { login() }
        //设置Google登录点击事件

        viewBinding.loginWithGoogle.setOnClickListener { login() }

        // 创建Tencent实例，传入应用的APPID
        tencent = Tencent.createInstance("102047280", applicationContext)
    }

    // 登录方法
    private fun login() {
        // 调用Tencent的login方法进行授权登录
        tencent.login(this, "all", loginListener)
    }

    // 定义登录监听器
    private val loginListener = object : LoginListener() {
        override fun onComplete(ret: Any) {
            // 当登录成功时，从返回结果中获取openid、access_token和过期时间
            val response = ret as JSONObject
            val openid = response.getString("openid")
            val accessToken = response.getString("access_token")
            val expiresIn = response.getLong("expires_in")

            // 将获取到的信息保存在Tencent实例中
            tencent.openId = openid
            tencent.setAccessToken(accessToken, expiresIn.toString())

            // 获取用户信息
            getUserInfo()
        }
    }

    // 获取用户信息的方法
    private fun getUserInfo() {
        // 创建UserInfo对象并请求用户信息
        val userInfo = UserInfo(applicationContext, tencent.qqToken)
        userInfo.getUserInfo(object : LoginListener() {
            override fun onComplete(any: Any) {
                // 当获取用户信息成功时，从返回结果中获取昵称和头像URL
                val response = any as JSONObject
                val nickname = response.optString("nickname")
                val avatar = response.optString("figureurl_2")

                // 保存用户信息
                save(nickname, avatar)
            }
        })
    }

    // 保存用户信息到服务器的方法

    private fun save(nickname: String, avatar: String) {
        // 使用协程异步调用API服务保存用户信息
        lifecycleScope.launch {
            val apiResult = ApiService.getService()
                .saveUser(nickname, avatar, tencent.openId, tencent.expiresIn)

            // 根据API返回的结果处理数据
            if (apiResult.success && apiResult.body != null) {
                // 如果成功，可以在这里做进一步的数据处理，比如保存到本地
                 UserManager.save(apiResult.body!!)
                // 并结束当前活动
                finish()
            } else {
                // 如果失败，在主线程中显示错误Toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 定义登录监听器基类，用于处理登录过程中的各种回调
    private open inner class LoginListener : IUiListener {
        // 当登录成功时被调用
        override fun onComplete(p0: Any) {
            // 这里可以添加额外的逻辑
        }

        // 当登录过程中出现错误时被调用
        override fun onError(err: UiError) {
            // 显示错误信息的Toast
            Toast.makeText(
                this@LoginActivity,
                "登录失败: ${err.errorMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 当用户取消登录操作时被调用
        override fun onCancel() {
            // 显示取消登录的Toast
            Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
        }

        // 当有警告信息时被调用，但在这个示例中没有处理
        override fun onWarning(p0: Int) {
        }
    }

    // 处理登录结果的回调方法
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 检查请求码是否为常量REQUEST_LOGIN，是则调用Tencent的 onActivityResultData 方法处理回调数据
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener)
        }
    }
}