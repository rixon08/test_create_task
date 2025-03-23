package com.simple.task.domain.usecase

import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.repository.TaskRepository

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskModel) = repository.addTask(task)
}