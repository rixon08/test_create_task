package com.simple.task.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.simple.task.R
import com.simple.task.utils.constants.BroadcastConstants
import com.simple.task.utils.extensions.toDate
import com.simple.task.utils.extensions.toStringDateFormat
import com.simple.task.presentation.ui.list.TaskListActivity
import java.util.Calendar

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val taskId = intent?.getIntExtra(BroadcastConstants.TASK_ID, 0) ?: return
        val taskName = intent?.getStringExtra(BroadcastConstants.TASK_NAME) ?: return
        showNotification(context, taskId, taskName)
    }

    private fun showNotification(context: Context, taskId: Int, taskName: String){
        val channelId = "task_channel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val intent = Intent(context, TaskListActivity::class.java).apply {
            putExtra(BroadcastConstants.TASK_ID,taskId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Deadline Reminder!")
            .setContentText("Task \"$taskName\" kurang dari 10 menit lagi!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId, notification)
        Log.d("notification", Calendar.getInstance().time.toString())
    }
}