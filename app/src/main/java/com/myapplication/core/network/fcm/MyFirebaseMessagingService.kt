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

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Este token es la dirección única de este celular para Firebase.
        // Se puede enviar a tu servidor aquí si el usuario ya está logueado.
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 1. Manejar mensajes que traen DATOS (más profesional)
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Aviso Escolar"
            val message = remoteMessage.data["body"] ?: ""
            val type = remoteMessage.data["type"] // Ejemplo: "NUEVO_PROFESOR"
            
            val finalTitle = if (type == "NUEVO_PROFESOR") "🎓 ¡Nuevo Profesor!" else title
            sendNotification(finalTitle, message)
        }

        // 2. Manejar notificaciones estándar de la consola
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "Escuela App", it.body ?: "")
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "school_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de la Escuela",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para que aparezca arriba
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
