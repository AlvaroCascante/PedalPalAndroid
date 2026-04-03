package com.quetoquenana.and.features.bikes.domain.model

data class StravaBike(
    val id: String,
    val name: String,
    val nickname: String?,
    val primary: Boolean,
    val retired: Boolean,
    val distance: Double?
)

