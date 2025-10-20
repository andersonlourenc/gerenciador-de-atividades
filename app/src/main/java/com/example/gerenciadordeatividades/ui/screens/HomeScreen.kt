package com.example.gerenciadordeatividades.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen() {

    val tasks = remember { mutableStateListOf<Task>() }
    var selectedFilter by remember { mutableStateOf("Todos") }

    fun addTask(title: String, description: String, status: TaskStatus = TaskStatus.PENDING) {
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val newTask = Task(
            id = tasks.size + 1,
            title = title,
            description = description,
            status = status,
            date = date
        )
        tasks.add(newTask)
    }

    val filteredTasks = when (selectedFilter) {
        "Pendentes" -> tasks.filter { it.status == TaskStatus.PENDING }
        "Em andamento" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
        "Concluídos" -> tasks.filter { it.status == TaskStatus.COMPLETED }
        else -> tasks
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addTask(
                        title = "Nova atividade ${tasks.size + 1}",
                        description = "Descrição atividade ${tasks.size + 1}"
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary


            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar atividade")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Gerenciador de Atividades",
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FilterButtonGrid(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                counts = mapOf(
                    "Todos" to tasks.size,
                    "Pendentes" to tasks.count { it.status == TaskStatus.PENDING },
                    "Em andamento" to tasks.count { it.status == TaskStatus.IN_PROGRESS },
                    "Concluídos" to tasks.count { it.status == TaskStatus.COMPLETED },

                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn (verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredTasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = task.description ?: "",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Data: ${task.date ?: "Sem data"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilterButtonGrid(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    counts: Map<String, Int>
) {
    val filtros = listOf(
        listOf("Todos", "Pendentes"),
        listOf("Em andamento", "Concluídos")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        filtros.forEach { linha ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                linha.forEach { filtro ->
                    FilterButton(
                        text = filtro,
                        selected = selectedFilter == filtro,
                        onClick = { onFilterSelected(filtro) },
                        count = counts[filtro] ?: 0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    count: Int,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = if (selected)
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        else
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(60.dp)
    ) {
        Text("$text ($count)")
    }
}

@Composable
fun TaskItem(task: Task) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = task.title, fontSize = 18.sp)
        Text(
            text = task.status.name,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        if (!task.description.isNullOrBlank()) {
            Text(
                text = task.description ?: "",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
