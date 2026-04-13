package com.myapplication.features.teacher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import com.myapplication.features.teacher.domain.usecases.CreateTeacherUseCase
import com.myapplication.features.teacher.domain.usecases.DeleteTeacherUseCase
import com.myapplication.features.teacher.domain.usecases.GetTeachersUseCase
import com.myapplication.features.teacher.domain.usecases.UpdateTeacherUseCase

class TeacherViewModelFactory(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val teacherRepository: TeacherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherViewModel(
                getTeachersUseCase,
                createTeacherUseCase,
                updateTeacherUseCase,
                deleteTeacherUseCase,
                teacherRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
