package com.medsy.pharmacy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.medsy.pharmacy.firebase.FCMTokenService
import com.medsy.presentation.R
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MedsyPharmacyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FCMTokenService.ORDERS_CHANNEL_ID,
                getString(R.string.notification_channel_orders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_orders_description)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
