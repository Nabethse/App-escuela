package com.myapplication.features.teacher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import com.myapplication.features.teacher.domain.usecases.CreateTeacherUseCase
import com.myapplication.features.teacher.domain.usecases.DeleteTeacherUseCase
import com.myapplication.features.teacher.domain.usecases.GetTeachersUseCase
import com.myapplication.features.teacher.domain.usecases.UpdateTeacherUseCase
import com.myapplication.features.auth.domain.repositories.AuthRepository

class TeacherViewModelFactory(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val teacherRepository: TeacherRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherViewModel(
                getTeachersUseCase,
                createTeacherUseCase,
                updateTeacherUseCase,
                deleteTeacherUseCase,
                teacherRepository,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
