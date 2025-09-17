package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.Image
import com.arnas.klatrebackend.dataclasses.ServiceResult
import org.springframework.web.multipart.MultipartFile

interface ImageServiceInterface {

    fun storeImageMetadata(userId: Long, boulderId: Long, contentType: String, size: Long): ServiceResult<String>
    fun getImageMetadata(imageId: String): ServiceResult<Image?>
    fun getImageMetadataByBoulder(boulderId: Long): ServiceResult<Image?>
    fun storeImageFile(file: MultipartFile, boulderId: Long,  userId: Long): ServiceResult<String>
    fun getImage(boulderId: Long): Image?
    fun deleteImage(image: Image)
}