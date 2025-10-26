package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.RouteResponse
import com.arnas.klatrebackend.dataclasses.RouteDTO
import org.springframework.web.multipart.MultipartFile

interface RouteServiceInterface {
    fun addRoute(userId: Long, routeDTO: RouteDTO): Long
    fun updateRoute(routeId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?)
    fun deleteRoute(routeId: Long)
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse
}