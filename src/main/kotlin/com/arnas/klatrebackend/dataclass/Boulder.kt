package com.arnas.klatrebackend.dataclass

import com.arnas.klatrebackend.dataclass.Image

data class Boulder (
    val id: Long,
    val name: String,
    val attempts: Int,
    val grade: String,
    var image: String?,
)
