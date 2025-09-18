package com.arnas.klatrebackend.dataclasses

data class Place(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupID: Long,
    val gradingSystem: Long
)

data class PlaceUpdateDTO(
    val placeId: Long,
    val name: String? = null,
    val description: String? = null,
    val gradingSystem: Long? = null,
    val groupID: Long? = null,
)

data class PlaceWithGrades(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupID: Long,
    val gradingSystem: GradingSystemWithGrades
)

data class PlaceWithBoulders(
    val place: Place,
    val boulders: List<Boulder>
)

data class PlaceRequest(
    val groupId: Long,
    val name: String,
    val description: String? = null
)
