package com.example.gerenciadordeatividades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gerenciadordeatividades.repository.TaskRepositoryImpl
import com.example.gerenciadordeatividades.ui.screens.HomeScreen
import com.example.gerenciadordeatividades.ui.theme.GerenciadorDeAtividadesTheme
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModelFactory
import com.example.gerenciadordeatividades.data.datastore.TaskManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            GerenciadorDeAtividadesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {

                            val context = LocalContext.current.applicationContext

                            val taskManager = TaskManager(context)
                            val taskRepository = TaskRepositoryImpl(taskManager)
                            val viewModelFactory = TaskViewModelFactory(repository = taskRepository)

                            val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)

                            HomeScreen(viewModel = viewModel)


                        }
                    }
                }
            }
        }
    }
}