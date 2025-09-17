package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclass.ServiceResult
import org.springframework.web.multipart.MultipartFile

interface BoulderServiceInterface {
    fun addBoulder(userId: Long, placeId: Long, boulderInfo: Map<String, String>): ServiceResult<Long>
    fun updateBoulder(boulderId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?): ServiceResult<String>
    fun deleteBoulder(boulderId: Long): ServiceResult<Unit>
}