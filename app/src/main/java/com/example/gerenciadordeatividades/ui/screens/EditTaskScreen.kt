package com.example.gerenciadordeatividades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskModal(
    taskToEdit: Task,
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description ?: "") }
    var selectedStatus by remember { mutableStateOf(taskToEdit.status) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val utcFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val initialDateMillis = taskToEdit.deadline
    val selectedDateString = remember { mutableStateOf(utcFormatter.format(Date(initialDateMillis))) }
    val selectedDateMillis = remember { mutableStateOf<Long?>(initialDateMillis)}

    val datePickerState = rememberDatePickerState(
        initialDateMillis
    )
    val showDatePicker = remember { mutableStateOf(false)}

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false

                        val selectedMillisUTC = datePickerState.selectedDateMillis
                        selectedDateMillis.value = selectedMillisUTC

                        if (selectedMillisUTC != null) {
                            selectedDateString.value = utcFormatter.format(Date(selectedMillisUTC))
                        } else {
                            selectedDateMillis.value = initialDateMillis
                            selectedDateString.value = utcFormatter.format(Date(initialDateMillis))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false}) { Text("Cancelar")}
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = "Editar Atividade",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da atividade") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Status:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskStatus.values().forEach { status ->
                    FilterChip(
                        selected = (selectedStatus == status),
                        onClick = { selectedStatus = status },
                        label = { Text(getStatusText(status)) }
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição da Atividade:") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            OutlinedTextField(
                value = selectedDateString.value,
                onValueChange = {},
                label = { Text("Data Limite") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker.value = true},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Selecionar data",
                        modifier = Modifier.clickable { showDatePicker.value = true }
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val deadlineLong = selectedDateMillis.value

                    if (title.isNotBlank() && deadlineLong != null) {
                        val updatedTask = taskToEdit.copy(
                            title = title.trim(),
                            description = description.trim().ifEmpty { null },
                            status = selectedStatus,
                            deadline = deadlineLong
                        )
                        viewModel.updateTask(updatedTask)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && selectedDateMillis.value != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Alterações")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
