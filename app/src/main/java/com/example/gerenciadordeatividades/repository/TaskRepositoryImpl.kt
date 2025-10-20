package com.example.gerenciadordeatividades.repository

import com.example.gerenciadordeatividades.data.datastore.TaskManager
import com.example.gerenciadordeatividades.domain.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val store: TaskManager) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> = store.tasks

    override suspend fun insertTask(task: Task) = store.insertTask(task)

    override suspend fun updateTask(task: Task) = store.updateTask(task)

    override suspend fun deleteTask(task: Task) = store.removeTaskById(task.id)

    override suspend fun deleteExpiredTasks(now: Long) = store.clearExpiredTasks(now)
}
