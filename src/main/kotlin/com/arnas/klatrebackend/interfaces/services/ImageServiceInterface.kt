package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.Image
import com.arnas.klatrebackend.dataclasses.ServiceResult
import org.springframework.web.multipart.MultipartFile

interface ImageServiceInterface {

    fun storeImageMetadata(userId: Long, contentType: String, size: Long): String
    fun getImageMetadata(imageId: String): Image?
    fun getImageMetadataById(id: String): Image?
    fun storeImageFile(file: MultipartFile,  userId: Long): String
    fun getImage(id: String): Image?
    fun deleteImage(id: String)
}