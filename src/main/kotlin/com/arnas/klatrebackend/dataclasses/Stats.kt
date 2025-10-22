package com.arnas.klatrebackend.dataclasses

//data class Stats()

data class UserGroupTotalStats(
    val groupId: Long,
    val time: String,
    val totalAttempts: Int,
)