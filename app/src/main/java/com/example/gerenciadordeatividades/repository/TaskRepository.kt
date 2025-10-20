package com.example.gerenciadordeatividades.repository

import com.example.gerenciadordeatividades.domain.model.Task
import kotlinx.coroutines.flow.Flow


interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteExpiredTasks(now: Long = System.currentTimeMillis())
}