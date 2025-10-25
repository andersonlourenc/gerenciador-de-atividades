package com.example.gerenciadordeatividades.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.lang.Exception
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.gerenciadordeatividades.ui.util.getStatusInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskModal(
    taskToEdit: Task,
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description ?: "") }
    var selectedStatus by remember { mutableStateOf(taskToEdit.status) }
    var imageUri by remember { mutableStateOf(taskToEdit.imageUri?.let { Uri.parse(it) }) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                    imageUri = uri
                } catch (e: Exception) {
                    imageUri = uri
                }
            }
        }
    )

    val todayUtcStartMillis = getTodayUtcStartMillis()
    val initialDateMillis = taskToEdit.deadline
    var selectedDateMillis by remember { mutableStateOf<Long?>(initialDateMillis) }
    var selectedDateString by remember(selectedDateMillis) {
        mutableStateOf(
            selectedDateMillis?.let {
                java.time.Instant.ofEpochMilli(it).atZone(utcZoneId).toLocalDate().format(displayDateFormatter)
            } ?: ""
        )
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayUtcStartMillis
                }
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false

                        val millisFromPicker = datePickerState.selectedDateMillis

                        if (millisFromPicker != null) {
                            selectedDateMillis = millisFromPicker
                            try {
                                selectedDateString = java.time.Instant.ofEpochMilli(millisFromPicker)
                                    .atZone(utcZoneId)
                                    .toLocalDate()
                                    .format(displayDateFormatter)

                            } catch (e: Exception) {

                                selectedDateString = "Erro data"
                                selectedDateMillis = null
                            }
                        } else {

                            selectedDateMillis = null
                            selectedDateString = ""
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp, top = 24.dp)
                .verticalScroll(scrollState),

            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(40.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da atividade") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant

                )
            )
            Spacer(modifier = Modifier.height(16.dp))


            ImageAndStatusRow(
                imageUri = imageUri,
                onPickImage = {
                    pickImageLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = { imageUri = null },
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição da Atividade:") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant

                ),
                trailingIcon = {
                    if (description.isNotEmpty()) {
                        IconButton(onClick = { description = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Limpar Descrição"
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDateString,
                onValueChange = {},
                label = { Text("Data Limite") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Selecionar data",
                        modifier = Modifier.clickable { showDatePicker  = true }
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val deadlineLong = selectedDateMillis ?: initialDateMillis

                    if (title.isNotBlank() && deadlineLong != null) {
                        val updatedTask = taskToEdit.copy(
                            title = title.trim(),
                            description = description.trim().ifEmpty { null },
                            status = selectedStatus,
                            deadline = deadlineLong,
                            imageUri = imageUri?.toString()
                        )
                        viewModel.updateTask(updatedTask)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank() && selectedDateMillis != null,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),

            ) {
                Text("Salvar Alterações")
            }
        }
    }
}


private val displayDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val utcZoneId: ZoneId = ZoneId.of("UTC")

private fun getTodayUtcStartMillis(): Long {
    return Instant.now()
        .atZone(utcZoneId)
        .toLocalDate()
        .atStartOfDay(utcZoneId)
        .toInstant()
        .toEpochMilli()
}

@Composable
fun ImageAndStatusRow(
    imageUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    selectedStatus: TaskStatus,
    onStatusSelected: (TaskStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text("Imagem:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { if (imageUri == null) onPickImage() else onRemoveImage() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (imageUri == null) Icons.Default.Image else Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (imageUri == null) "Adicionar" else "Remover")
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text("Status:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, getStatusInfo(selectedStatus).second),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = getStatusInfo(selectedStatus).second
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (statusText, statusColor) = getStatusInfo(selectedStatus)

                    Text(
                        text = statusText,
                        color = statusColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = statusColor
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TaskStatus.values().forEach { status ->
                        val (statusText, statusColor) = getStatusInfo(status)

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = statusText,
                                    color = statusColor
                                )
                            },
                            onClick = {
                                onStatusSelected(status)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}