package com.example.gerenciadordeatividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.gerenciadordeatividades.data.datastore.TaskManager
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskManager = TaskManager(this)

        lifecycleScope.launch {

            val newTask = Task(
                id = 2,
                title = "Testreqe",
                description = "Teste datastore",
                status = TaskStatus.PENDING,
                deadline = System.currentTimeMillis() + 86_400_000
            )
            taskManager.addTask(newTask)
            Log.d("DATASTORE", "Tarefa salva: $newTask")

            taskManager.tasks.collect { tasks ->
                Log.d("DATASTORE", "Tarefas lidas: $tasks")
            }
        }
    }
}

