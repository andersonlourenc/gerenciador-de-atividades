package com.example.gerenciadordeatividades

import kotlinx.coroutines.test.runTest
import androidx.datastore.core.DataStoreFactory
import com.example.gerenciadordeatividades.data.datastore.TaskSerializer
import com.example.gerenciadordeatividades.domain.model.Task
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import org.junit.Test
import java.io.File


class TaskManagerTest {

    @Test
    fun salvarETestarLeituraDeTarefas() = runTest {
        val file = File("tasks_test.json")
        val dataStore = DataStoreFactory.create(
            serializer = TaskSerializer,
            produceFile = { file }
        )

        val task = Task(
            id = 1,
            title = "Criar UI",
            description = "interface de maneira intuitiva",
        )

        dataStore.updateData { current -> current + task }

        val tasks = dataStore.data.first()

        assertTrue(tasks.any { it.title == "Criar UI"})
    }

}