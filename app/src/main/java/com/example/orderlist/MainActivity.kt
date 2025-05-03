package com.example.orderlist

import android.Manifest // <-- Импорт
import android.content.pm.PackageManager // <-- Импорт
import android.os.Build // <-- Импорт
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts // <-- Импорт
import androidx.activity.viewModels
import androidx.core.content.ContextCompat // <-- Импорт
import com.example.orderlist.ui.theme.OrderListTheme


import com.example.ui.authorization.AuthViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                println("Notification permission granted.")
            } else {
                println("Notification permission denied.")

            }
        }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                println("Notification permission already granted.")

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

                println("Showing rationale for notification permission.")

            } else {

                println("Requesting notification permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        askNotificationPermission()

        setContent {
            OrderListTheme {
                MainHostScreen()
            }
        }
    }
}