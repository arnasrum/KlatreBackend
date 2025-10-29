package com.arnas.klatrebackend.dataclasses
import org.springframework.web.multipart.MultipartFile

data class Route (
    val id: Long,
    val name: String,
    val gradeId: Long,
    val placeId: Long,
    var description: String?,
    val active: Boolean,
    var imageId: String?,
)

data class RouteDTO(
    val name: String,
    val gradeId: Long,
    val placeId: Long,
    var description: String?,
    val active: Boolean?,
    var image: MultipartFile?,
)

data class RouteUpdateDTO(
    val routeId: Long,
    val name: String?,
    val gradeId: Long?,
    val placeId: Long?,
    var description: String?,
    val active: Boolean?,
    var image: MultipartFile?,
)

data class RouteResponse(
    val boulders: List<Route>,
    val page: Int,
    val limit: Int,
    val activeBouldersCount: Int,
    val retiredBouldersCount: Int,
    val hasMore: Boolean,
)