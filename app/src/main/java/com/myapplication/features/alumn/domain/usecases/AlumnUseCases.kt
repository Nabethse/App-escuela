package com.myapplication.features.alumn.domain.usecases

import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import com.myapplication.features.alumn.domain.repositories.AlumnRepository

class GetAlumnsUseCase(private val repository: AlumnRepository) {
    suspend operator fun invoke(token: String) = repository.getAlumns(token)
}

class CreateAlumnUseCase(private val repository: AlumnRepository) {
    suspend operator fun invoke(token: String, alumn: AlumnDto) = repository.createAlumn(token, alumn)
}

class UpdateAlumnUseCase(private val repository: AlumnRepository) {
    suspend operator fun invoke(token: String, id: Int, alumn: AlumnDto) = repository.updateAlumn(token, id, alumn)
}

class DeleteAlumnUseCase(private val repository: AlumnRepository) {
    suspend operator fun invoke(token: String, id: Int) = repository.deleteAlumn(token, id)
}