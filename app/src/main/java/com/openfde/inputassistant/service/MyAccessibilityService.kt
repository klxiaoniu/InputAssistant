package com.openfde.inputassistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.openfde.inputassistant.Constants
import com.openfde.inputassistant.util.ConfigUtil
import com.openfde.inputassistant.util.DeviceUtil

class MyAccessibilityService : AccessibilityService() {
    private var currentPackage: String? = null

    override fun onServiceConnected() {
        instance = this
        Log.d(TAG, "onServiceConnected")
        val intent = Intent().setAction(Constants.ACTION_UPDATE_STATUS).apply {
            putExtra("isRunning", true)
        }
        sendBroadcast(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        Log.d(TAG, "onAccessibilityEvent: $event")
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                event.packageName?.let {
                    currentPackage = it.toString()
                    Log.d(TAG, currentPackage!!)
                    val intent = Intent().setAction(Constants.ACTION_UPDATE_PACKAGE).apply {
                        putExtra("packageName", currentPackage)
                    }
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event ?: return false
        Log.d(TAG, "onKeyEvent: $event")
        if (ConfigUtil.targetPackage != currentPackage) return false
        if (event.action == KeyEvent.ACTION_DOWN) {
            return when (event.keyCode) {
                KeyEvent.KEYCODE_W -> {
                    val path = Path().apply {
                        moveTo(
                            (DeviceUtil.getWidth() * 0.5).toFloat(),
                            (DeviceUtil.getHeight() * 0.7).toFloat()
                        )
                        lineTo(
                            (DeviceUtil.getWidth() * 0.5).toFloat(),
                            (DeviceUtil.getHeight() * 0.3).toFloat()
                        )
                    }
                    sendPath(path)
                }

                KeyEvent.KEYCODE_A -> {
                    val path = Path().apply {
                        moveTo(
                            (DeviceUtil.getWidth() * 0.8).toFloat(),
                            (DeviceUtil.getHeight() * 0.5).toFloat()
                        )
                        lineTo(
                            (DeviceUtil.getWidth() * 0.2).toFloat(),
                            (DeviceUtil.getHeight() * 0.5).toFloat()
                        )
                    }
                    sendPath(path)
                }

                KeyEvent.KEYCODE_S -> {
                    val path = Path().apply {
                        moveTo(
                            (DeviceUtil.getWidth() * 0.5).toFloat(),
                            (DeviceUtil.getHeight() * 0.3).toFloat()
                        )
                        lineTo(
                            (DeviceUtil.getWidth() * 0.5).toFloat(),
                            (DeviceUtil.getHeight() * 0.7).toFloat()
                        )
                    }
                    sendPath(path)
                }

                KeyEvent.KEYCODE_D -> {
                    val path = Path().apply {
                        moveTo(
                            (DeviceUtil.getWidth() * 0.2).toFloat(),
                            (DeviceUtil.getHeight() * 0.5).toFloat()
                        )
                        lineTo(
                            (DeviceUtil.getWidth() * 0.8).toFloat(),
                            (DeviceUtil.getHeight() * 0.5).toFloat()
                        )
                    }
                    sendPath(path)
                }

                else -> false
            }
        }
        return false
    }

    override fun onInterrupt() {
        instance = null
        Log.d(TAG, "onInterrupt")
        val intent = Intent().setAction(Constants.ACTION_UPDATE_STATUS).apply {
            putExtra("isRunning", false)
        }
        sendBroadcast(intent)

    }

    override fun onDestroy() {
        instance = null
        Log.d(TAG, "onDestroy")
        val intent = Intent().setAction(Constants.ACTION_UPDATE_STATUS).apply {
            putExtra("isRunning", false)
        }
        sendBroadcast(intent)

    }

    private fun sendPath(path: Path): Boolean {
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()
        dispatchGesture(gesture, null, null)
        return true
    }

    companion object {
        private var TAG = "MyAccessibilityService"
        private var instance: MyAccessibilityService? = null

        fun isStarted(): Boolean {
            return instance != null
        }

        fun stop() {
            if (isStarted()) {
                instance!!.disableSelf()
            }
        }
    }
}