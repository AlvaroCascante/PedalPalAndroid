package com.quetoquenana.and.features.auth.data.remote.dto.request

import com.quetoquenana.and.features.auth.domain.model.CreateUserRequest

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