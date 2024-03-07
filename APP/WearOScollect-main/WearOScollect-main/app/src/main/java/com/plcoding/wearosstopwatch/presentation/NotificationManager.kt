package com.plcoding.wearosstopwatch.presentation

//https://developer.android.com/develop/ui/views/notifications/navigation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.plcoding.wearosstopwatch.R

class NotificationManager(private val context: Context) {

    private val channelId = "primary_notification_channel"
    private val channelName = "Primary Notification Channel"
    private val channelDescription = "This channel shall handle all the prompt-notifications for the user"

    fun promptNotification(notificationTimeId: Long, questions: List<TemplateQuestion>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }

        createNotificationChannel()

        val notificationIntent = Intent(context, LabelActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_NEW_TASK// or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notificationIntent.putExtra("NotificationTimeId", notificationTimeId)

        //Inserts TemplateInfos class handed over by @MainActivity as typedArray to put as Intent Extra
        if (questions != null) {
            Log.i("Manager", "questions")
            notificationIntent.putExtra("questions", questions.toTypedArray())
            Log.i("Manager", "questionsAfter")
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,
            notificationTimeId.toInt(), notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Zeit zu interagieren!")
            //.setContentText("Wie fühlst du dich gerade?")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setTimeoutAfter(30000) // will remove the notification after 30 seconds
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set notification priority to high
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationTimeId.toInt(), notification.build())
            Log.i("Notify", "in brackets")
        }
        Log.i("Notify", "manager")
    }

    //Creates oneTimeNotification which are not used at the moment
    fun oneTimeNotification(title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }

        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(1)
            //.setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setTimeoutAfter(15000) // will remove the notification after 3 Minutes
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            notify(123, notification.build())
            Log.i("Notify", "onetime")
        }
        Log.i("Notify", "manager")
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelDescription
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        //added to set mode to vibration
        notificationChannel.enableVibration(true)
    }

    //TODO Method to cancel the notification
    fun cancelNotification() {

    }
}
