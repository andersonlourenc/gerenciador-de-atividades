package com.example.gerenciadordeatividades.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gerenciadordeatividades.domain.model.Task
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(it, flag)
                    imageUri = it
                } catch (e: SecurityException) {
                    Log.e("ImagePicker", "Erro ao obter permissão: ${e.message}")
                    e.printStackTrace()
                    imageUri = it
                }
            }
        }
    )

    val todayUtcStartMillis = getTodayUtcStartMillis()
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedDateString by remember(selectedDateMillis) {
        mutableStateOf(
            selectedDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(utcZoneId)
                    .toLocalDate().format(displayDateFormatter)
            } ?: ""
        )
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= todayUtcStartMillis
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
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                            selectedDateString = Instant.ofEpochMilli(it)
                                .atZone(utcZoneId)
                                .toLocalDate()
                                .format(displayDateFormatter)
                        } ?: run {
                            selectedDateMillis = null
                            selectedDateString = ""
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    ModalBottomSheet(onDismiss, sheetState = sheetState, dragHandle = null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Adicionar Atividade",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da atividade") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImagePickerRow(
                    imageUri = imageUri,
                    onPickImage = {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemoveImage = { imageUri = null }
                )

            }

            Spacer(Modifier.height(16.dp))

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

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDateString,
                onValueChange = {},
                label = { Text("Data limite") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Selecionar data",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    selectedDateMillis?.let { deadline ->
                        if (title.isNotBlank()) {
                            val newTask = Task(
                                id = UUID.randomUUID().toString(),
                                title = title.trim(),
                                description = description.trim().ifEmpty { null },
                                status = TaskStatus.PENDING, // padrão
                                deadline = deadline,
                                imageUri = imageUri?.toString()
                            )
                            viewModel.insertTask(newTask)
                            onDismiss()
                        }
                    }
                },
                enabled = title.isNotBlank() && selectedDateMillis != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Criar Atividade")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImagePickerRow(
    imageUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Imagem:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { if (imageUri == null) onPickImage() else onRemoveImage() },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth(0.5f),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
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
}

private val displayDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val utcZoneId: ZoneId = ZoneId.of("UTC")

private fun getTodayUtcStartMillis(): Long =
    Instant.now().atZone(utcZoneId).toLocalDate().atStartOfDay(utcZoneId).toInstant().toEpochMilli()
