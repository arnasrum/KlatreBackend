package com.arnas.klatrebackend.dataclasses

data class ClimbingSession (
    val id: Long,
    val groupId: Long,
    val userId: Long,
    val placeId: Long,
    val startDate: String,
    val name: String,
    val routeAttempts: List<RouteAttempt>
)

data class ClimbingSessionDTO (
    val groupId: Long,
    val userId: Long,
    val placeId: Long,
    val startDate: String,
    val name: String?,
    val routeAttempts: List<RouteAttempt>
)

data class RouteAttempt(
    val attempts: Int,
    val completed: Boolean,
    val routeId: Long
)