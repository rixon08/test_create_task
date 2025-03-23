package com.simple.task.di

import com.simple.task.data.local.TaskDatabase
import com.simple.task.data.notification.TaskAlarmScheduler
import com.simple.task.data.repository.TaskRepositoryImpl
import com.simple.task.domain.repository.TaskRepository
import com.simple.task.domain.usecase.AddTaskUseCase
import com.simple.task.domain.usecase.DeleteTaskUseCase
import com.simple.task.domain.usecase.EditTaskUseCase
import com.simple.task.domain.usecase.GetTasksSortedByClosestDeadline
import com.simple.task.domain.usecase.GetTasksUseCase
import com.simple.task.domain.usecase.SearchTaskUseCase
import com.simple.task.presentation.ui.addedit.AddEditTaskViewModel
import com.simple.task.presentation.ui.list.TaskViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { TaskDatabase.getInstance(androidContext()) }

    single { TaskAlarmScheduler(androidContext()) }

    single { get<TaskDatabase>().taskDao() }

    single <TaskRepository> { TaskRepositoryImpl(get(), get()) }

    factory { GetTasksUseCase(get()) }
    factory { SearchTaskUseCase(get()) }
    factory { AddTaskUseCase(get()) }
    factory { EditTaskUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }
    factory { GetTasksSortedByClosestDeadline(get()) }


    viewModel { TaskViewModel(get(), get(), get(), get(), get()) }
    viewModel { AddEditTaskViewModel(get(), get()) }

}