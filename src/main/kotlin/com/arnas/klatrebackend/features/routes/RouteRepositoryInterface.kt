package com.arnas.klatrebackend.features.routes

interface RouteRepositoryInterface {
    fun getRouteById(routeId: Long): Route?
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route>
    fun addRoute(routeDTO: RouteDTO, imageId: String?, userId: Long): Long
    fun updateRoute(route: Route): Int
    fun deleteRoute(routeId: Long): Int
    fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int
    fun getRoutesByPlaceId(placeId: Long): List<Route>
}