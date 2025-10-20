package com.example.gerenciadordeatividades.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.gerenciadordeatividades.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.taskDataStore: DataStore<List<Task>> by dataStore(
    fileName = "tasks.json",
    serializer = TaskSerializer
)

class TaskManager(private val context: Context) {

    val tasks: Flow<List<Task>> = context.taskDataStore.data
        .catch { emit(emptyList()) }
        .map { it }

    suspend fun insertTask(task: Task) {
        context.taskDataStore.updateData { currentTasks ->
            currentTasks + task
        }
    }

    suspend fun removeTaskById(id: Int) {
        context.taskDataStore.updateData { currentTasks ->
            currentTasks.filterNot { it.id == id }
        }
    }

    suspend fun updateTask(updated: Task) {
        context.taskDataStore.updateData { currentTasks ->
            currentTasks.map { if (it.id == updated.id) updated else it }
        }
    }

    suspend fun clearExpiredTasks(currentTime: Long) {
        context.taskDataStore.updateData { currentTasks ->
            currentTasks.filter { it.deadline?.let { d -> d >= currentTime } ?: true }
        }
    }
}
