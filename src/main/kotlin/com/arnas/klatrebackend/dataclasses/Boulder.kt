package com.arnas.klatrebackend.dataclasses

data class Boulder (
    val id: Long,
    val name: String,
    val grade: Long,
    val place: Long,
    var image: String?,
    var description: String?,
)

data class BoulderRequest(
    val name: String,
    val grade: Long,
    val place: Long,
    val description: String?
)

data class BoulderWithSend(
    val boulder: Boulder,
    val routeSend: RouteSend? = null
)