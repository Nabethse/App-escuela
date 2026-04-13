package com.myapplication.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
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
            ListenableWorker.Result.success()
        } catch (e: Exception) {
            ListenableWorker.Result.retry()
        }
    }
}
