package com.arnas.klatrebackend.dataclasses

data class Place(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupId: Long,
    val gradingSystemId: Long
)

data class PlaceUpdateDTO(
    val placeId: Long,
    val name: String? = null,
    val description: String? = null,
    val gradingSystemId: Long? = null,
    val groupId: Long? = null,
)

data class PlaceWithGrades(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupId: Long,
    val gradingSystemId: GradingSystemWithGrades
)

data class PlaceRequest(
    val groupId: Long,
    val name: String,
    val description: String? = null
)
