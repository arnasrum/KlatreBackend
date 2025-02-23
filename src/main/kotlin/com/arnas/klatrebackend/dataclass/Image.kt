package com.arnas.klatrebackend.dataclass

data class Image(
    val id: Long,
    val image: String, // Base64
    val boulder: Long,
)
