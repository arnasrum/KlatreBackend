package com.arnas.klatrebackend.dataclass

data class Boulder (
    val id: Long,
    val name: String,
    val attempts: Int,
    val grade: String,
    val place: Long,
    var image: String?,
)

data class BoulderRequest(
    val name: String,
    val attempts: Int,
    val grade: String,
    val place: Long,
)