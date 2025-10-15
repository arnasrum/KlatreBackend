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
    val userId: Long,
    val groupId: Long,
    val placeId: Long,
    val timestamp: Long,
)

data class ActiveSession(
    val id: Long,
    val groupId: Long,
    val userId: Long,
    val placeId: Long,
)

data class RouteAttempt(
    val id: Long,
    val attempts: Int,
    val completed: Boolean,
    val routeId: Long,
    val timestamp: String,
    val session: Long
)

data class UpdateAttemptRequest(
    val id: Long,
    val attempts: Int,
    val completed: Boolean,
    val timestamp: Long
)

data class RouteAttemptDTO(
    val attempts: Int,
    val completed: Boolean,
    val routeId: Long,
    val timestamp: String,
    val session: Long
)


data class RouteAttemptDisplay(
    val id: Long,
    val attempts: Int,
    val completed: Boolean,
    val route: String,
    val grade: String,
    val timestamp: String
)