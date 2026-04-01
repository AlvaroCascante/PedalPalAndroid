package com.quetoquenana.and.features.authentication.data.remote.dto

import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest

data class CreatePersonRequestDto(
    val idNumber: String,
    val name: String,
    val lastname: String,
)

data class CreateUserRequestDto(
    val nickname: String,
    val person: CreatePersonRequestDto,
)

fun CreateUserRequest.toDto(): CreateUserRequestDto {
    return CreateUserRequestDto(
        nickname = nickname,
        person = CreatePersonRequestDto(
            idNumber = idNumber,
            name = name,
            lastname = lastname
        )
    )
}