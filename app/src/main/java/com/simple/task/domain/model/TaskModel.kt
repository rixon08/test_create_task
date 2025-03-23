package com.simple.task.domain.model

import com.simple.task.extensions.toStringDateFormat
import java.util.Date


data class TaskModel (
    val id: Int = 0,
    val name: String,
    val deadline: Long,
    val isCompleted: Boolean
){
    val stringDateFormat: String
        get() {
            try {
                val date = Date(deadline)
                return date.toStringDateFormat()
            } catch (e: Exception){
                return ""
            }
        }
}