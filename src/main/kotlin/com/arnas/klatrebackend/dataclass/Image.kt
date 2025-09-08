package com.arnas.klatrebackend.dataclass

data class Image(
    val id: String,
    val contentType: String,
    val fileSize: Long,
    val boulder: Long,

) {
    fun getUrl(): String = "/api/images/$id"
}
