package com.example.myapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var hideButton: Button
    private lateinit var showButton: Button

    companion object {
        const val ENABLE_APP_ACTION = "com.example.myapp.ENABLE_APP"
        const val DISABLE_APP_ACTION = "com.example.myapp.DISABLE_APP"
    }

    private val enableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            toggleLauncherComponentState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        }
    }

    private val disableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            toggleLauncherComponentState(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideButton = findViewById(R.id.hideButton)
        showButton = findViewById(R.id.showButton)

        hideButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hide App")
                .setMessage("The app will disappear from the launcher. You can restore it using a broadcast command or another method.")
                .setPositiveButton("Hide") { _, _ ->
                    toggleLauncherComponentState(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        showButton.setOnClickListener {
            toggleLauncherComponentState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        }
    }

    override fun onResume() {
        super.onResume()
        val enableFilter = IntentFilter(ENABLE_APP_ACTION)
        val disableFilter = IntentFilter(DISABLE_APP_ACTION)

        registerReceiver(enableReceiver, enableFilter, Context.RECEIVER_NOT_EXPORTED)
        registerReceiver(disableReceiver, disableFilter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(enableReceiver)
        unregisterReceiver(disableReceiver)
    }

    private fun toggleLauncherComponentState(newState: Int) {
        val componentName = ComponentName(this, MainActivity::class.java)
        val packageManager = packageManager
        try {
            packageManager.setComponentEnabledSetting(
                componentName,
                newState,
                PackageManager.DONT_KILL_APP
            )
            Log.d("MainActivity", "Launcher state changed to $newState")
        } catch (e: Exception) {
            e.printStackTrace()
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to change app visibility. Please try again.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
