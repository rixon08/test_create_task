package com.simple.task.presentation.ui.addedit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.task.utils.constants.AddEditTaskKeyConstants
import com.simple.task.domain.model.TaskModel
import com.simple.task.domain.usecase.AddTaskUseCase
import com.simple.task.domain.usecase.EditTaskUseCase
import com.simple.task.utils.extensions.toLong
import com.simple.task.presentation.ui.addedit.AddEditTaskActivity.Companion
import kotlinx.coroutines.launch
import java.util.Date

class AddEditTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val editTaskUseCase: EditTaskUseCase
) : ViewModel() {
    private var task: TaskModel? = null

    private val _isTaskNameError = MutableLiveData<Boolean>()
    val isTaskNameError: LiveData<Boolean> get() = _isTaskNameError

    private val _isDeadlineError = MutableLiveData<Boolean>()
    val isDeadlineError: LiveData<Boolean> get() = _isDeadlineError

    fun saveTask(taskName: String, selectedDeadline: Date?, isCompleted: Boolean, task: TaskModel?, onDone: (Boolean) -> Unit){
        if (validateInputs(taskName, selectedDeadline)) {
            if (task == null) {
                addTask(
                    TaskModel(
                        name = taskName,
                        deadline = selectedDeadline!!.toLong()!!,
                        isCompleted = isCompleted
                    )
                ) {
                    onDone(true)
                }
            } else {
                editTask(
                    TaskModel(
                        id = task.id,
                        name = taskName,
                        deadline = selectedDeadline!!.toLong()!!,
                        isCompleted = isCompleted
                    )
                ) {
                    onDone(true)
                }
            }
        } else {
            onDone(false)
        }
    }

    fun validateInputs(taskName: String, selectedDeadline: Date?): Boolean {
        var isValid = true

        if (taskName.isEmpty()) {
            _isTaskNameError.value = true
            isValid = false
        } else {
            _isTaskNameError.value = false
        }

        if (selectedDeadline == null) {
            _isDeadlineError.value = true
            isValid = false
        } else {
            _isDeadlineError.value = false
        }

        return isValid
    }

    fun addTask(task: TaskModel, onDone: () -> Unit ) {
        viewModelScope.launch {
            addTaskUseCase(task)
            onDone()
        }
    }

    fun editTask(task: TaskModel, onDone: () -> Unit ) {
        viewModelScope.launch {
            editTaskUseCase(task)
            onDone()
        }
    }
}