package com.simple.task.data.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.simple.task.utils.constants.BroadcastConstants
import com.simple.task.domain.model.TaskModel
import com.simple.task.utils.extensions.toDate
import com.simple.task.utils.extensions.toStringDateFormat
import com.simple.task.presentation.receiver.TaskAlarmReceiver

class TaskAlarmScheduler(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTaskNotification(task: TaskModel) {
        val deadline = task.deadline - (10 * 60 * 1000) // 10 Menit Sebelum
        val now = System.currentTimeMillis()
        if (deadline < now) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra(BroadcastConstants.TASK_ID, task.id)
            putExtra(BroadcastConstants.TASK_NAME, task.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            deadline,
            pendingIntent
        )
    }

    fun scheduleCancel(task: TaskModel){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra(BroadcastConstants.TASK_ID, task.id)
            putExtra(BroadcastConstants.TASK_NAME, task.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}