package com.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapplication.presentation.screens.LoginScreen
import com.myapplication.presentation.screens.RegisterScreen
import com.myapplication.presentation.viewmodels.AuthViewModel
import com.myapplication.presentation.viewmodels.AuthViewModelFactory
import com.myapplication.ui.theme.InventarioAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioAPPTheme {
                val navController = rememberNavController()
                val appContainer = (application as EscuelaApp).container
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(
                        appContainer.loginUseCase,
                        appContainer.registerUseCase
                    )
                )
                val authState by authViewModel.authState.collectAsState()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
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
                            onRegister = { name, email, password ->
                                authViewModel.register(name, email, password)
                            }
                        )
                    }
                }
            }
        }
    }
}
