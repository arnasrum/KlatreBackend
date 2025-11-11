package com.arnas.klatrebackend.dataclasses

//data class Stats()

data class UserGroupSessionStats(
    val year: Int,
    val month: Int,
    val day: Int,
    val routesTried: Int,
    val totalTries: Int,
    val totalCompleted: Int,
    val groupId: Long,
    val userId: Long,
)