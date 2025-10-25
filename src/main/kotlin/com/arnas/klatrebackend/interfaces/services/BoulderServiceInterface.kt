package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderResponse
import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.dataclasses.ServiceResult
import org.springframework.web.multipart.MultipartFile

interface BoulderServiceInterface {
    fun addBoulder(userId: Long, routeDTO: RouteDTO): Long
    fun updateBoulder(routeId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?)
    fun deleteBoulder(routeId: Long): Unit
    fun getBouldersByPlace(placeId: Long, page: Int, limit: Int): BoulderResponse
}