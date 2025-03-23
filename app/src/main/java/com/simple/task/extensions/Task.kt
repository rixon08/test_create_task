package com.simple.task.extensions

import com.simple.task.data.local.TaskEntity
import com.simple.task.domain.model.TaskModel

fun TaskEntity.toDomain(): TaskModel {
    return TaskModel(id, name, deadline, isCompleted)
}

fun TaskModel.toEntity(): TaskEntity {
    return TaskEntity(id, name, deadline, isCompleted)
}