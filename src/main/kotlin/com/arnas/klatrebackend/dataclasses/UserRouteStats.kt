package com.arnas.klatrebackend.dataclasses

data class UserRouteStats(
    val userId: Long,
    val hardestRouteId: Long,
    val totalAttempts: Int,
)
