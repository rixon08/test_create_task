package com.simple.task.data.repository

import androidx.lifecycle.LiveData
import com.simple.task.data.local.TaskDao
import com.simple.task.data.notification.TaskAlarmScheduler
import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.repository.TaskRepository
import com.simple.task.utils.extensions.toDomain
import com.simple.task.utils.extensions.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepositoryImpl(private val dao: TaskDao, private val taskAlarmScheduler: TaskAlarmScheduler) : TaskRepository {
    override suspend fun addTask(task: TaskModel) {
        dao.insertTask(task.toEntity())
    }

    override suspend fun getAllTasks(): List<TaskModel> {
        return withContext(Dispatchers.IO) {
            val allTask = dao.getAllTasks().map { it.toDomain() }
            val now = System.currentTimeMillis()
            val notCompeltedTask = dao.getTasksSortedByClosestDeadline(now).map { it.toDomain() }
            for (task in notCompeltedTask) {
                taskAlarmScheduler.scheduleTaskNotification(task)
            }
            allTask
        }
    }

    override suspend fun searchTask(query: String): List<TaskModel> {
        return withContext(Dispatchers.IO) {
            dao.searchTask("%$query%").map { it.toDomain() }
        }
    }

    override suspend fun deleteTask(task: TaskModel) {
        dao.deleteTask(task.toEntity())
        taskAlarmScheduler.scheduleCancel(task)
    }

    override suspend fun updateTask(task: TaskModel) {
        dao.updateTask(task.toEntity())
        if (!task.isCompleted) taskAlarmScheduler.scheduleTaskNotification(task)
    }

    override suspend fun getTasksSortedByClosestDeadline(): List<TaskModel> {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            dao.getTasksSortedByClosestDeadline(now).map { it.toDomain() }
        }
    }

}