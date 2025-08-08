package com.arnas.klatrebackend.dataclass

data class Place(
    val id: Long,
    val name: String,
    val description: String? = null,
)