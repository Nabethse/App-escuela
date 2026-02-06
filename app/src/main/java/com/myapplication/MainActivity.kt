package com.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapplication.features.alumn.presentation.screens.AlumnsScreen
import com.myapplication.features.alumn.presentation.viewmodel.AlumnViewModel
import com.myapplication.features.auth.presentation.screens.LoginScreen
import com.myapplication.features.auth.presentation.screens.RegisterScreen
import com.myapplication.features.auth.presentation.viewmodel.AuthState
import com.myapplication.features.auth.presentation.viewmodel.AuthViewModel
import com.myapplication.features.home.presentation.screens.HomeScreen
import com.myapplication.features.teacher.presentation.screens.TeachersScreen
import com.myapplication.features.teacher.presentation.viewmodel.TeacherViewModel
import com.myapplication.ui.theme.InventarioAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioAPPTheme {
                val navController = rememberNavController()
                val appContainer = (application as EscuelaApp).container
                
                // Usando el método Factory del appContainer
                val authViewModel: AuthViewModel = viewModel(
                    factory = appContainer.authViewModelFactory
                )
                val authState by authViewModel.authState.collectAsState()
                
                val token = (authState as? AuthState.Success)?.token ?: ""

                LaunchedEffect(authState) {
                    if (authState is AuthState.Success) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            authState = authState,
                            onLogin = { email, password ->
                                authViewModel.login(email, password)
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            authState = authState,
                            onRegister = { name, email, password ->
                                authViewModel.register(name, email, password)
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToTeachers = { navController.navigate("teachers") },
                            onNavigateToAlumns = { navController.navigate("alumns") },
                            onLogout = { 
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("teachers") {
                        val teacherViewModel: TeacherViewModel = viewModel(
                            factory = appContainer.teacherViewModelFactory
                        )
                        TeachersScreen(viewModel = teacherViewModel, token = token)
                    }
                    composable("alumns") {
                        val alumnViewModel: AlumnViewModel = viewModel(
                            factory = appContainer.alumnViewModelFactory
                        )
                        AlumnsScreen(viewModel = alumnViewModel, token = token)
                    }
                }
            }
        }
    }
}