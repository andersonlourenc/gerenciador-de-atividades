package com.example.gerenciadordeatividades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TaskViewModel, navController: NavHostController) {

    val tasks by viewModel.tasks.collectAsState()
    var showAddTaskModal by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    var expandedMenuTaskId by remember { mutableStateOf<String?>(null) }
    var taskToEdit: Task? by remember{ mutableStateOf<Task?>(null) }

    var showDeleteConfirmationDialog by remember { mutableStateOf<Task?>(null) }

    val filteredTasks = when (selectedFilter) {
        "Pendentes" -> tasks.filter { it.status == TaskStatus.PENDING }
        "Em andamento" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
        "Concluídos" -> tasks.filter { it.status == TaskStatus.COMPLETED }
        else -> tasks
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Gerenciador de Atividades",
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddTaskModal = true
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

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredTasks) { task ->

                    var formatter = remember {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                    }
                    val dataFormadata = formatter.format(Date(task.deadline))
                    val isMenuExpanded = expandedMenuTaskId == task.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable {
                                        navController.navigate("details/${task.id}")
                                    }
                            ) {
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
                                    text = "Data Limite: $dataFormadata",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Box(
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                            ) {
                                IconButton(onClick = { expandedMenuTaskId = task.id }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Opções da Atividade"
                                    )
                                }
                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { expandedMenuTaskId = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Editar Atividade") },
                                        onClick = {
                                            taskToEdit = task
                                            expandedMenuTaskId = null
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Excluir Atividade", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showDeleteConfirmationDialog = task
                                            expandedMenuTaskId = null
                                        },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Excluir",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    if (showAddTaskModal) {
        AddTaskModal(
            onDismiss = {
                showAddTaskModal = false
            },
            viewModel = viewModel
        )
    }

    val currentTaskToEdit = taskToEdit
    if (currentTaskToEdit != null) {
        EditTaskModal(
            taskToEdit = currentTaskToEdit,
            onDismiss = { taskToEdit = null },
            viewModel = viewModel
        )
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