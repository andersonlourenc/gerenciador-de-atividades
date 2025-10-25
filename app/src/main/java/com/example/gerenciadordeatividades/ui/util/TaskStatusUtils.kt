package com.example.gerenciadordeatividades.ui.util

import androidx.compose.ui.graphics.Color
import com.example.gerenciadordeatividades.domain.model.TaskStatus
import com.example.gerenciadordeatividades.ui.theme.*

fun getStatusInfo(status: TaskStatus): Pair<String, Color> {
    return when (status) {
        TaskStatus.PENDING -> "Pendente" to Pending
        TaskStatus.IN_PROGRESS -> "Em andamento" to InProgress
        TaskStatus.COMPLETED -> "Concluído" to Completed
    }
}