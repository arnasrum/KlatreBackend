package com.arnas.klatrebackend.dataclass

data class Image(
    val name: String,
    val id: Long,
    val imageData: ByteArray,
)