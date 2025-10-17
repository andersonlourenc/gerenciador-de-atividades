package com.example.gerenciadordeatividades.data.datastore

import androidx.datastore.core.Serializer
import com.example.gerenciadordeatividades.domain.model.Task
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TaskSerializer : Serializer<List<Task>> {

    override val defaultValue: List<Task>
        get() = emptyList()

    override suspend fun readFrom(input: InputStream): List<Task> {
        return try {
            Json.decodeFromString(
                ListSerializer(Task.serializer()),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: List<Task>, output: OutputStream) {
        output.write(
            Json.encodeToString(ListSerializer(Task.serializer()), t)
                .encodeToByteArray()
        )
    }
}
