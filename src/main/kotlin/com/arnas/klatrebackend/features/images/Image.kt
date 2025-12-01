package com.arnas.klatrebackend.features.images

data class Image(
    val id: String,
    val contentType: String,
    val fileSize: Long,
)