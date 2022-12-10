package com.example.moodmonitoringapp.fragments.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.moodmonitoringapp.fragments.Notification.ScheduledWorker.Companion.NOTIFICATION_MESSAGE
import com.example.moodmonitoringapp.fragments.Notification.ScheduledWorker.Companion.NOTIFICATION_TITLE

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val title = it.getStringExtra(NOTIFICATION_TITLE)
            val message = it.getStringExtra(NOTIFICATION_MESSAGE)

            // Create Notification Data
            val notificationData = Data.Builder()
                .putString(NOTIFICATION_TITLE, title)
                .putString(NOTIFICATION_MESSAGE, message)
                .build()

            // Init Worker
            val work = OneTimeWorkRequest.Builder(ScheduledWorker::class.java)
                .setInputData(notificationData)
                .build()

            // Start Worker
            WorkManager.getInstance().beginWith(work).enqueue()

            Log.d(javaClass.name, "WorkManager is Enqueued.")
        }
    }
}
