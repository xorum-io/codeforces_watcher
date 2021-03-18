package com.bogdan.codeforceswatcher.handlers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.MainActivity
import io.xorum.codeforceswatcher.redux.middlewares.NotificationHandler

class AndroidNotificationHandler : NotificationHandler {

    override fun handle(notificationData: List<Pair<String, Int>>) {
        val notificationText = buildNotificationText(notificationData)

        if (notificationText.isNotEmpty()) {
            showNotification(CwApp.app, notificationText)
        }
    }

    private fun buildNotificationText(notificationData: List<Pair<String, Int>>) =
            notificationData.joinToString(separator = "\n") { ratingChange ->
                "${ratingChange.first} ${stringifyRatingChangeValue(ratingChange.second)}"
            }

    private fun stringifyRatingChangeValue(ratingChange: Int) = if (ratingChange < 0) ratingChange else "+$ratingChange"

    private fun showNotification(context: Context, text: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intentReceiver = Intent(context, MainActivity::class.java)
        intentReceiver.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(
                context, 0, intentReceiver,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel(context, notificationManager)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_antivirus)
                .setContentTitle(context.getString(R.string.ratings_have_been_updated))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)

        val notification = builder.build()

        notificationManager.notify(1, notification)
    }

    @RequiresApi(android.os.Build.VERSION_CODES.O)
    private fun createNotificationChannel(
            context: Context,
            notificationManager: NotificationManager,
    ) {

        val channel = NotificationChannel(
                CHANNEL_ID, context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(R.string.ratings_update)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "1234"
    }
}
