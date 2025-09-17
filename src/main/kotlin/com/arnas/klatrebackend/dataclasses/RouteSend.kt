package com.arnas.klatrebackend.dataclasses

data class RouteSend(
    val id: Long,
    val userID: Long,
    val boulderID: Long,
    val attempts: Int,
    val completed: Boolean = false,
    val perceivedGrade: String?
)
