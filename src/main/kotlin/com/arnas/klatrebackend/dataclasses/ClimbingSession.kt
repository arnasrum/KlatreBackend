package com.arnas.klatrebackend.dataclasses

data class ClimbingSession (
    val id: Long,
    val groupId: Long,
    val placeId: Long,
    val startDate: String,
    val name: String,
    val routeSends: List<RouteSend>

)