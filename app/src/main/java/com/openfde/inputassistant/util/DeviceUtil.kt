package com.openfde.inputassistant.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.openfde.inputassistant.MyApplication

object DeviceUtil {
    @Suppress("DEPRECATION")
    private val outMetrics by lazy {
        val displayMetrics = DisplayMetrics()
        val windowManager =
            MyApplication.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        displayMetrics
    }

    fun getWidth() = outMetrics.widthPixels

    fun getHeight() = outMetrics.heightPixels
}