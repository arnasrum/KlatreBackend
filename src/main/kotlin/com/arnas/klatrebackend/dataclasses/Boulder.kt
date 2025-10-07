package com.arnas.klatrebackend.dataclasses

data class Boulder (
    val id: Long,
    val name: String,
    val grade: Long,
    val place: Long,
    val active: Boolean,
    var image: String?,
    var description: String?,
)

data class BoulderRequest(
    val name: String,
    val grade: Long,
    val place: Long,
    val description: String?
)

data class BoulderResponse(
    val boulders: List<Boulder>,
    val page: Int,
    val limit: Int,
    val activeBouldersCount: Int,
    val retiredBouldersCount: Int,
    val hasMore: Boolean,
)