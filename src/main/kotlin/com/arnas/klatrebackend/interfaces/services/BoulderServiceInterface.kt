package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.ServiceResult
import org.springframework.web.multipart.MultipartFile

interface BoulderServiceInterface {
    fun addBoulder(userId: Long, placeId: Long, name: String, grade: Long, description: String?): ServiceResult<Long>
    fun updateBoulder(boulderId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?): ServiceResult<String>
    fun deleteBoulder(boulderId: Long): ServiceResult<Unit>
}