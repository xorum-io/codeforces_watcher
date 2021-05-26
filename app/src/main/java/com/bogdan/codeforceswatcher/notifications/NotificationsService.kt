package com.bogdan.codeforceswatcher.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationsService : FirebaseMessagingService() {

    private val channelId = "Default"

    private enum class NotificationType {
        RATING_UPDATES
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val text = message.data["text"] ?: return
        val notificationType = NotificationType.valueOf(message.data["notificationType"] ?: return)
        val notification = getNotification(text, notificationType)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.default_channel), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notification)
    }

    private fun getNotification(text: String, notificationType: NotificationType): Notification = when (notificationType) {
        NotificationType.RATING_UPDATES -> {
            val showTaskIntent = Intent(applicationContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val contentIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    showTaskIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            NotificationCompat.Builder(this, channelId)
                    .setContentTitle(getString(R.string.ratings_have_been_updated))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                    .setSmallIcon(R.mipmap.ic_launcher_antivirus)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .build()
        }
    }

    override fun onNewToken(newToken: String) {}
}