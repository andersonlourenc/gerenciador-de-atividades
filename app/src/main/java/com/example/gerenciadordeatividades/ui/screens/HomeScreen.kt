package com.example.gerenciadordeatividades.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.util.getStatusInfo
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TaskViewModel, navController: NavHostController) {

    val tasks by viewModel.tasks.collectAsState()
    var showAddTaskModal by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    var expandedMenuTaskId by remember { mutableStateOf<String?>(null) }
    var taskToEdit: Task? by remember { mutableStateOf<Task?>(null) }

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
                    Text(text = "Gerenciador de Atividades")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskModal = true },
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

                    val formatter = remember {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                    }
                    val dataFormatada = formatter.format(Date(task.deadline))
                    val isMenuExpanded = expandedMenuTaskId == task.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("details/${task.id}") },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )

                                Box {
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
                                            text = {
                                                Text(
                                                    "Excluir Atividade",
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            },
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

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Data limite: $dataFormatada",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                StatusChip(task.status)
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    if (showAddTaskModal) {
        AddTaskModal(onDismiss = { showAddTaskModal = false }, viewModel = viewModel)
    }

    taskToEdit?.let {
        EditTaskModal(
            taskToEdit = it,
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
                modifier = Modifier.fillMaxWidth()
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

    val containerColor: Color
    val contentColor: Color
    val border: BorderStroke?

    if (selected) {
        containerColor = MaterialTheme.colorScheme.background
        contentColor = MaterialTheme.colorScheme.primary
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else {
        containerColor = MaterialTheme.colorScheme.primary
        contentColor = MaterialTheme.colorScheme.onPrimary
        border = null
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(56.dp),
        border = border
    ) {
        Text("$text ($count)")
    }
}

@Composable
fun StatusChip(status: TaskStatus) {

    val (label, textColor) = getStatusInfo(status)

    Surface(
        color = Color.Transparent,
        border = BorderStroke(1.dp, textColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}