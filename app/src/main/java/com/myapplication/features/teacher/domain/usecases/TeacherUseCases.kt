package com.myapplication.features.teacher.domain.usecases

import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import javax.inject.Inject

class GetTeachersUseCase @Inject constructor(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String) = repository.getTeachers(token)
}

class CreateTeacherUseCase @Inject constructor(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, teacher: TeacherDto) = repository.createTeacher(token, teacher)
}

class UpdateTeacherUseCase @Inject constructor(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, id: Int, teacher: TeacherDto) = repository.updateTeacher(token, id, teacher)
}

class DeleteTeacherUseCase @Inject constructor(private val repository: TeacherRepository) {
    suspend operator fun invoke(token: String, id: Int) = repository.deleteTeacher(token, id)
}
