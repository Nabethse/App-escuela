package com.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapplication.core.util.BiometricAuthenticator
import com.myapplication.features.alumn.presentation.screens.AlumnsScreen
import com.myapplication.features.alumn.presentation.viewmodel.AlumnViewModel
import com.myapplication.features.auth.presentation.screens.LoginScreen
import com.myapplication.features.auth.presentation.screens.RegisterScreen
import com.myapplication.features.auth.presentation.viewmodel.AuthState
import com.myapplication.features.auth.presentation.viewmodel.AuthViewModel
import com.myapplication.features.attendance.presentation.screens.AttendanceScreen
import com.myapplication.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.myapplication.features.home.presentation.screens.HomeScreen
import com.myapplication.features.teacher.presentation.screens.TeachersScreen
import com.myapplication.features.teacher.presentation.viewmodel.TeacherViewModel
import com.myapplication.ui.theme.InventarioAPPTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var biometricAuthenticator: BiometricAuthenticator

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No recibirás notificaciones push", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        askNotificationPermission()

        setContent {
            InventarioAPPTheme {
                val navController = rememberNavController()
                
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsState()
                val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState()

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
                            isBiometricEnabled = isBiometricEnabled,
                            onLogin = { email, password ->
                                authViewModel.login(email, password)
                            },
                            onNavigateToRegister = {
                                authViewModel.resetState()
                                navController.navigate("register")
                            },
                            onBiometricLogin = {
                                if (biometricAuthenticator.isBiometricAvailable()) {
                                    biometricAuthenticator.promptBiometric(
                                        activity = this@MainActivity,
                                        title = "Inicio de Sesión Biométrico",
                                        subtitle = "Usa tu huella digital o PIN para entrar",
                                        onSuccess = {
                                            authViewModel.loginWithBiometrics()
                                        },
                                        onError = { _, err ->
                                            Toast.makeText(this@MainActivity, "Error: $err", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailed = {
                                            Toast.makeText(this@MainActivity, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(this@MainActivity, "Biometría no disponible en este dispositivo", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onToggleBiometric = { enabled ->
                                authViewModel.setBiometricEnabled(enabled)
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
                                authViewModel.resetState()
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToTeachers = { navController.navigate("teachers") },
                            onNavigateToAlumns = { navController.navigate("alumns") },
                            onNavigateToAttendance = { navController.navigate("attendance") },
                            onLogout = { 
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("attendance") {
                        val attendanceViewModel: AttendanceViewModel = hiltViewModel()
                        AttendanceScreen(viewModel = attendanceViewModel, onNavigateBack = { navController.popBackStack() })
                    }
                    composable("teachers") {
                        val teacherViewModel: TeacherViewModel = hiltViewModel()
                        val token = (authState as? AuthState.Success)?.token ?: ""
                        TeachersScreen(viewModel = teacherViewModel, token = token)
                    }
                    composable("alumns") {
                        val alumnViewModel: AlumnViewModel = hiltViewModel()
                        AlumnsScreen(viewModel = alumnViewModel)
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
