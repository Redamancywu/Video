package com.techme.jetpack.ext

import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner

interface IViewBinding {
    fun getLayoutInflater(): LayoutInflater

    fun getLifecycleOwner(): LifecycleOwner
}
