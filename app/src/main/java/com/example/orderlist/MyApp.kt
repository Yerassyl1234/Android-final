package com.example.orderlist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.WashCompletionWorker
import com.example.data.dataModule
import com.example.ui.authorization.authModule
import com.example.ui.booking.bookingModule
import com.example.ui.profile.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        startKoin {
            // Логгер Koin (полезно для отладки, используйте Level.ERROR или Level.NONE для релиза)
            androidLogger(Level.DEBUG)
            // Предоставляем Android Context для Koin
            androidContext(this@MyApp)
            // Загружаем наши модули
            modules(
                dataModule,
                authModule,
                bookingModule,
                profileModule,
                )
        }
        println("Koin Started")
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(WashCompletionWorker.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            println("Notification channel created.")
        }
    }
}
