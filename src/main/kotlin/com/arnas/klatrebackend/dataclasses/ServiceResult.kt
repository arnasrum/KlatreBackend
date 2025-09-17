package com.arnas.klatrebackend.dataclasses

data class ServiceResult<T>(
    val data: T? = null,
    val success: Boolean = true,
    val message: String? = null,
    val errorCode: String? = null
)
