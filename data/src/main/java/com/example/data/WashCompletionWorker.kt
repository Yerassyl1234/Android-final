package com.example.data

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.orderlist.data.R


class WashCompletionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "wash_completion_channel"
    }

    override suspend fun doWork(): Result {
        val machineNumber = inputData.getInt("MACHINE_NUMBER", -1)
        if (machineNumber == -1) {
            return Result.failure()
        }
        return try {
            showNotification(applicationContext, machineNumber)
            Result.success()
        } catch (e: Exception) {
            println("Error showing notification: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(context: Context, machineNumber: Int) {
        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent? = if (launchIntent != null) {
            PendingIntent.getActivity(
                context,
                machineNumber,
                launchIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bell_icon)
            .setContentTitle("Стирка завершена!")
            .setContentText("Стирка на машине №$machineNumber окончена.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                println("Notification permission not granted!")
                return
            }
        }

        with(NotificationManagerCompat.from(context)) {
            val notificationId = machineNumber + System.currentTimeMillis().toInt()
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify(notificationId, builder.build())
                println("Notification shown for machine $machineNumber")
            } else {
                println("Skipping notification for machine $machineNumber due to missing permission.")
            }
        }
    }
}
