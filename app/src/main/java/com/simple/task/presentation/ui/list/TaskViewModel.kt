package com.simple.task.presentation.ui.list

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.repository.TaskRepository
import com.simple.task.domain.usecase.DeleteTaskUseCase
import com.simple.task.domain.usecase.EditTaskUseCase
import com.simple.task.domain.usecase.GetTasksSortedByClosestDeadline
import com.simple.task.domain.usecase.GetTasksUseCase
import com.simple.task.domain.usecase.SearchTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val searchTaskUseCase: SearchTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    private val getTasksSortedByClosestDeadline: GetTasksSortedByClosestDeadline
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val _tasks = MutableLiveData<List<TaskModel>>(emptyList())
    val tasks: LiveData<List<TaskModel>> get() = _tasks

    init {
        observeSearchQuery()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = getTasksUseCase() ?: emptyList()

        }
    }

    fun searchTask(query: String) {
        viewModelScope.launch {
            _tasks.value = searchTaskUseCase(query) ?: emptyList()
        }
    }

    fun deleteTask(task: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
            loadTasks()
        }
    }

    fun editCompleted(task: TaskModel){
        viewModelScope.launch {
            Log.d("edit completed","${task.id}: ${task.name} ${task.stringDateFormat} ${task.isCompleted}")
            editTaskUseCase(task)
            loadTasks()
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    viewModelScope.launch {
                        _tasks.value = if (query.isBlank()) {
                            getTasksUseCase()
                        } else {
                            searchTaskUseCase(query)
                        }
                    }
                }
        }
    }

}