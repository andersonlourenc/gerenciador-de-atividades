package com.example.gerenciadordeatividades.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Calendar

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

    suspend fun removeTaskById(id: String) {
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
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfTodayMillis = calendar.timeInMillis

        context.taskDataStore.updateData { currentTasks ->

            currentTasks.filter { task ->
                task.status == TaskStatus.COMPLETED || task.deadline >= startOfTodayMillis
            }
        }
    }
}