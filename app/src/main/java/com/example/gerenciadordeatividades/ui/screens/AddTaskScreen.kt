package com.example.gerenciadordeatividades.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskModal(
    onDismiss: () -> Unit,
    viewModel: TaskViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                    imageUri = uri
                } catch (e: SecurityException) {
                    Log.e("ImagePicket", "Erro ao obter permissão: $${e.message}")
                    e.printStackTrace()
                    imageUri = uri
                }
            }
        }
    )

    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                    imageUri = uri
                } catch (e: Exception) {
                    Log.e("ImagePicker", "Erro ao obter permissão (GetContent): ${e.message}")
                    e.printStackTrace()
                    imageUri = uri

                }
            }
        }
    )

    val todayUtcStartMillis = getTodayUtcStartMillis()

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedDateString by remember(selectedDateMillis) {
        mutableStateOf(
            selectedDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(utcZoneId).toLocalDate().format(displayDateFormatter)
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
                                selectedDateString = Instant.ofEpochMilli(millisFromPicker)
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
        onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp, top = 8.dp)
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
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da atividade") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
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

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição da Atividade:") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant

                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {

                    TextButton(
                        onClick = {
                            if(ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
                                pickImageLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                getContentLauncher.launch("image/*")
                            }
                        }, contentPadding = PaddingValues(start = 4.dp, end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Image, "Anexar Imagem",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adicionar imagem", style = MaterialTheme.typography.labelMedium)
                    }
                }
                IconButton(onClick = { description = ""}) {
                    Icon(
                        Icons.Default.Clear, "Limpar Descrição"
                    )
                }
            }

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
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant

                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    val deadlineLong = selectedDateMillis
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

                enabled = title.isNotBlank() && selectedDateMillis != null,
                modifier = Modifier.fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),

            ) {
                Text("Criar Atividade")
            }

            Spacer(modifier = Modifier.height(16.dp))

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