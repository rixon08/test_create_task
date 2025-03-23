package com.simple.task.domain.repository

import androidx.lifecycle.LiveData
import com.simple.task.data.local.TaskDao
import com.simple.task.domain.model.TaskModel

interface TaskRepository {
    suspend fun addTask(task: TaskModel)
    suspend fun getAllTasks(): List<TaskModel>
    suspend fun searchTask(query: String): List<TaskModel>
    suspend fun deleteTask(task: TaskModel)
    suspend fun updateTask(task: TaskModel)
    suspend fun getTasksSortedByClosestDeadline(): List<TaskModel>
}