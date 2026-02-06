package com.myapplication.features.teacher.domain.usecases

import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository

class GetTeachersUseCase(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String) = repository.getTeachers(token)
}

class CreateTeacherUseCase(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, teacher: TeacherDto) = repository.createTeacher(token, teacher)
}

class UpdateTeacherUseCase(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, id: Int, teacher: TeacherDto) = repository.updateTeacher(token, id, teacher)
}

class DeleteTeacherUseCase(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, id: Int) = repository.deleteTeacher(token, id)
}