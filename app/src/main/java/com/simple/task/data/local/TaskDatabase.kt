package com.simple.task.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object{
        @Volatile private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase{
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_db"
                    )
                        .fallbackToDestructiveMigration() // 🚀 Menangani perubahan schema
                        .build()

                    INSTANCE = instance
                    Log.d("TaskDatabase", "Database instance created ✅")
                    instance
                } catch (e: Exception) {
                    Log.e("TaskDatabase", "Database creation failed ❌", e)
                    throw e
                }
        }   }
    }

}