package com.myapplication.core.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.myapplication.R
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

//se ejecuta  en segundo plano cada 15 minutos
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alumnRepository: AlumnRepository,
    private val teacherRepository: TeacherRepository,
    private val attendanceRepository: AttendanceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result {
        return try {
            // Estrategia: Sincronizar datos en segundo plano
            alumnRepository.getAlumns("")
            teacherRepository.getTeachers("")
            attendanceRepository.syncPendingAttendance("")

            showSyncNotification()

            ListenableWorker.Result.success()
        } catch (e: Exception) {
            ListenableWorker.Result.retry()
        }
    }

    private fun showSyncNotification() {
        val channelId = "sync_notifications"
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sincronización de Datos",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Sincronización Completa")
            .setContentText("Los datos de la escuela se han actualizado correctamente.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
