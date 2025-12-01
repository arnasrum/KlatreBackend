package com.arnas.klatrebackend.features.routes

interface RouteServiceInterface {
    fun addRoute(userId: Long, routeDTO: RouteDTO): Long
    fun updateRoute(routeDTO: RouteUpdateDTO, userId: Long)
    fun deleteRoute(routeId: Long)
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse
}