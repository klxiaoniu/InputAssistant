package com.openfde.inputassistant.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.openfde.inputassistant.Constants
import com.openfde.inputassistant.databinding.ActivityMainBinding
import com.openfde.inputassistant.service.MyAccessibilityService
import com.openfde.inputassistant.util.ConfigUtil


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnGrant.setOnClickListener {
            if (!MyAccessibilityService.isStarted()) {
                try {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, "已启动", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnStop.setOnClickListener {
            MyAccessibilityService.stop()
        }

        binding.btnSave.setOnClickListener {
            ConfigUtil.targetPackage = binding.etTarget.text.toString().trim()
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.ACTION_UPDATE_STATUS ->
                        binding.tvStatus.text =
                            if (intent.getBooleanExtra("isRunning", false)) "运行中" else "已停止"

                    Constants.ACTION_UPDATE_PACKAGE ->
                        binding.tvPackage.text = intent.getStringExtra("packageName")
                }
            }

        }
        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_UPDATE_STATUS)
            addAction(Constants.ACTION_UPDATE_PACKAGE)
        }
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }
}