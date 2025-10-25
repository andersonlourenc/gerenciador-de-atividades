package com.example.gerenciadordeatividades

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gerenciadordeatividades.repository.TaskRepositoryImpl
import com.example.gerenciadordeatividades.ui.screens.HomeScreen
import com.example.gerenciadordeatividades.ui.screens.TaskDetailsScreen
import com.example.gerenciadordeatividades.ui.theme.GerenciadorDeAtividadesTheme
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModel
import com.example.gerenciadordeatividades.ui.viewmodel.TaskViewModelFactory
import com.example.gerenciadordeatividades.data.datastore.TaskManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GerenciadorDeAtividadesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    val context = LocalContext.current.applicationContext
                    val taskManager = TaskManager(context)
                    val taskRepository = TaskRepositoryImpl(taskManager)
                    val viewModelFactory = TaskViewModelFactory(repository = taskRepository)

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {

                            val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)

                            HomeScreen(
                                viewModel = viewModel, navController = navController
                            )
                        }

                        composable("details/{taskId}",
                            arguments = listOf(navArgument("taskId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->

                            val taskId = backStackEntry.arguments?.getString("taskId")

                            if (taskId != null) {
                                val viewModel: TaskViewModel = viewModel(factory = viewModelFactory)

                                TaskDetailsScreen(
                                    viewModel = viewModel,
                                    taskId = taskId,
                                    navController = navController
                                )
                            } else {
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}