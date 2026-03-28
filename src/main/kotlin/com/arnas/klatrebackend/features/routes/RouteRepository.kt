package com.arnas.klatrebackend.features.routes

import java.util.Optional

interface RouteRepository {
    fun getRouteById(routeId: Long): Optional<Route>
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route>
    fun getRoutesByPlace(placeId: Long): List<Route>
    fun addRoute(routeDTO: RouteDTO, imageId: String?, userId: Long): Long
    fun updateRoute(route: Route): Int
    fun deleteRoute(routeId: Long): Int
    fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int
}

