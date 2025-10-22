package com.example.gerenciadordeatividades.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class  Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val deadline: Long,
    val imageUri: String? = null

)

@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
