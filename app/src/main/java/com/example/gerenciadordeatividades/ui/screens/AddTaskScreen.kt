package com.example.gerenciadordeatividades.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())}
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val showDatePicker = remember { mutableStateOf(false) }
    val selectedDateString = remember { mutableStateOf("") }
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }

    if (showDatePicker.value) {
        DatePickerDialog(
            { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false

                        val selectedMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()

                        selectedDateMillis.value = selectedMillis
                        selectedDateString.value = formatter.format(Date(selectedMillis))
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker.value = false }
                ) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    ModalBottomSheet(
        onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nova Atividade",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da atividade") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição da Atividade:") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDateString.value,
                onValueChange = {},
                label = { Text("Data Limite") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker.value = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Selecionar data",
                        modifier = Modifier.clickable { showDatePicker.value = true }
                    )
                }

            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    val deadlineLong = selectedDateMillis.value

                    if (title.isNotBlank()) {

                        val newTask = Task(
                            id = UUID.randomUUID().toString(),
                            title = title.trim(),
                            description = description.trim().ifEmpty { null },
                            status = TaskStatus.PENDING,
                            deadline = deadlineLong!!,
                            imageUri = null
                        )

                        viewModel.insertTask(newTask)
                        onDismiss()
                    }
                },

                enabled = title.isNotBlank() && selectedDateMillis.value != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Atividade")
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}