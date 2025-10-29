package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.RouteResponse
import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.dataclasses.RouteUpdateDTO

interface RouteServiceInterface {
    fun addRoute(userId: Long, routeDTO: RouteDTO): Long
    fun updateRoute(routeDTO: RouteUpdateDTO, userId: Long)
    fun deleteRoute(routeId: Long)
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse
}