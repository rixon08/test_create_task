package com.simple.task.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.simple.task.domain.model.TaskModel

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE name LIKE :query")
    fun searchTask(query: String): List<TaskEntity>

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 and deadline > :now ORDER BY ABS(deadline - :now) ASC")
    suspend fun getTasksSortedByClosestDeadline(now: Long): List<TaskEntity>
}