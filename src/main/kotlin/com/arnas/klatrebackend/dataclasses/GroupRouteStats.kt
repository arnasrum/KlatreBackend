package com.arnas.klatrebackend.dataclasses

data class GroupRouteStats(
    val groupId: Long,
    val totalAttempts: Int,
    val totalCompleted: Int,
    val hardestRouteCompletedId: Long,
)
