package com.arnas.klatrebackend.features.routes

interface RouteService {
    fun addRoute(routeDTO: RouteDTO, userId: Long): Long
    fun updateRoute(routeDTO: RouteUpdateDTO, userId: Long)
    fun deleteRoute(routeId: Long)
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse
}

