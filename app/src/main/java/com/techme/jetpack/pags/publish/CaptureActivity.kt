// 包名，通常对应于应用的命名空间和目录结构
package com.techme.jetpack.pags.publish

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.techme.jetpack.ext.invokeViewBinding
import com.techme.jetpack_android_online.databinding.ActivityLayoutCaptureBinding
import com.techme.jetpack_android_online.R

// 定义CaptureActivity类，继承自AppCompatActivity
class CaptureActivity : AppCompatActivity() {

    // 声明Camera对象，稍后初始化
    private lateinit var camera: Camera

    // 使用View Binding获取布局文件中的视图引用
    private val binding: ActivityLayoutCaptureBinding by invokeViewBinding()

    // 伴生对象，用于静态方法和常量
    companion object {
        // 动态权限数组，根据设备的API版本添加不同的权限
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA, // 相机权限
            Manifest.permission.RECORD_AUDIO, // 录音权限
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) Manifest.permission.WRITE_EXTERNAL_STORAGE else null // 外部存储写入权限，仅在Android 9及更早版本需要
        ).filterNotNull().toTypedArray()

        // Spring动画参数，用于UI过渡效果
        private const val SPRING_STIFENESS_ALPHA_OUT = 100f
        private const val SPRING_STIFFNESS = 800f
        private const val SPRING_DAMPING_RATIO = 0.35f

        // 文件命名和存储路径常量
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-sss"
        private const val PHOTO_TYPE = "image/jpeg"
        private const val VIDEO_TYPE = "video/mp4"
        private const val RELATIVE_PATH_PICTURE = "Pictures/Jetpack"
        private const val RELATIVE_PATH_VIDEO = "Movies/Jetpack"

        // 请求码
        internal const val REQ_CAPTURE = 10001
        private const val PERMISSION_CODE = 1000

        // 输出文件信息的键
        internal const val RESULT_FILE_PATH = "file_path"
        internal const val RESULT_FILE_HEIGHT = "file_height"
        internal const val RESULT_FILE_WIDTH = "file_width"
        internal const val RESULT_FILE_TYPE = "file_type"

        // 启动CaptureActivity的方法
        fun startActivityForResult(activity: Activity) {
            val intent = Intent(activity, CaptureActivity::class.java)
            activity.startActivityForResult(intent, REQ_CAPTURE)
        }
    }

    // 当Activity创建时调用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置布局视图
        setContentView(binding.root)
        // 请求必要的权限
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE)
    }

    // 当权限请求的结果返回时调用
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            // 检查是否有被拒绝的权限
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }
            // 如果有权限被拒绝，则显示对话框
            if (deniedPermissions.isNotEmpty()) {
                showPermissionDialog(deniedPermissions)
            } else {
                // 如果所有权限都被授予，则启动相机
                startCamera()
            }
        }
    }

    // 显示权限对话框的私有方法
    private fun showPermissionDialog(deniedPermissions: List<String>) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.capture_permission_message))
            .setNegativeButton(getString(R.string.capture_permission_no)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setPositiveButton(getString(R.string.capture_permission_ok)) { dialog, _ ->
                ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), PERMISSION_CODE)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // 开始相机预览的私有方法
    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        // 获取ProcessCameraProvider实例
        val processCameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // 在主线程执行器上监听ProcessCameraProvider实例
        processCameraProviderFuture.addListener({
            val cameraProvider = processCameraProviderFuture.get()
            // 确定使用后置或前置摄像头
            val cameraSelector = when {
                cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> throw IllegalStateException("Back and Front camera are unavailable")
            }
            // 创建预览用例并设置目标旋转
            val preview = Preview.Builder()
                .setCameraSelector(cameraSelector)
                .setTargetRotation(binding.previewView.display.rotation)
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }
            // 解绑所有已绑定的用例
            cameraProvider.unbindAll()
            // 绑定预览用例到生命周期
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview
            )
        }, ContextCompat.getMainExecutor(this))
    }
}