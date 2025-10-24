package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest

interface BoulderRepositoryInterface {
    fun getRouteById(routeId: Long): Boulder?
    fun getBouldersByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Boulder>
    fun addBoulder(name: String, grade: Long, place: Long, description: String?, active: Boolean?, imageId: String?, userId: Long): Long
    fun updateBoulder(routeId: Long, name: String?, grade: Long?, place: Long?, description: String?, active: Boolean?, imageId: String?): Int
    fun deleteBoulder(routeId: Long): Int
    fun getNumBouldersInPlace(placeId: Long, countActive: Boolean): Int
}