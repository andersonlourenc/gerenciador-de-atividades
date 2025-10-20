package com.example.gerenciadordeatividades.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class  Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val deadline: Long? = null,
    val imageUri: String? = null,
    val finalized: Boolean = false,
    val date: String
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}