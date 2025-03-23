package com.simple.task.domain.usecase

import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.repository.TaskRepository

class SearchTaskUseCase (private val repository: TaskRepository){
    suspend operator fun invoke(query: String) = repository.searchTask(query)
}