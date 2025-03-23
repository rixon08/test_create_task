package com.simple.task.domain.usecase

import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.repository.TaskRepository

class GetTasksSortedByClosestDeadline(private val repository: TaskRepository) {
    suspend operator fun invoke(): List<TaskModel> = repository.getTasksSortedByClosestDeadline()
}