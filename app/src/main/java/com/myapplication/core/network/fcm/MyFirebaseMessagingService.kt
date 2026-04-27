package com.myapplication.core.network.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.core.data.UserPreferencesRepository
import com.myapplication.features.auth.domain.repositories.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var authRepository: AuthRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 1. Manejar si viene como payload de DATA (Recomendado para customizar)
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "Escuela App"
            val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
            sendNotification(title, body)
        } 
        // 2. Manejar si viene como payload de NOTIFICATION
        else {
            remoteMessage.notification?.let {
                sendNotification(it.title ?: "Escuela App", it.body ?: "")
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch {
            userPreferencesRepository.saveFcmToken(token)
            
            val userToken = userPreferencesRepository.userToken.first()
            if (!userToken.isNullOrBlank()) {
                try {
                    authRepository.updateFcmToken("Bearer $userToken", token)
                } catch (e: Exception) {
                    // Log error if needed
                }
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "canal_estudiantes"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de que el icono sea blanco/transparente
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de la escuela"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
