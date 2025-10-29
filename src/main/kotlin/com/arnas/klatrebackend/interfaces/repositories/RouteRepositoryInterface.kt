package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Route
import com.arnas.klatrebackend.dataclasses.RouteDTO

interface RouteRepositoryInterface {
    fun getRouteById(routeId: Long): Route?
    fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route>
    fun addRoute(routeDTO: RouteDTO, imageId: String?, userId: Long): Long
    fun updateRoute(route: Route): Int
    fun updateRoute(routeId: Long, name: String?, grade: Long?, place: Long?, description: String?, active: Boolean?, imageId: String?): Int
    fun deleteRoute(routeId: Long): Int
    fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int
    fun getRoutesByPlaceId(placeId: Long): List<Route>
}