package com.example.gerenciadordeatividades.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()


    init {
        clearExpiredTasks()

        loadTasks()
    }
    fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks().collect { tasksList ->

                val sortedList = tasksList.sortedBy { it.deadline }
                val sortedListFinal = sortedList.sortedBy { it.status == TaskStatus.COMPLETED }

                _tasks.value = sortedListFinal
            }
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun updateTaskStatus(taskToUpdate: Task, newStatus: TaskStatus) {
        viewModelScope.launch {
            val updatedTask = taskToUpdate.copy(status = newStatus)
            repository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearExpiredTasks() {
        viewModelScope.launch {
            repository.deleteExpiredTasks(System.currentTimeMillis())
        }
    }


}