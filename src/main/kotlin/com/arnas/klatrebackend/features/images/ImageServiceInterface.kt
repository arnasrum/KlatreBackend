package com.arnas.klatrebackend.features.images

import com.arnas.klatrebackend.features.images.Image
import org.springframework.web.multipart.MultipartFile

interface ImageServiceInterface {

    fun storeImageMetadata(userId: Long, contentType: String, size: Long): String
    fun getImageMetadata(imageId: String): Image?
    fun getImageMetadataById(id: String): Image?
    fun storeImageFile(file: MultipartFile, userId: Long): String
    fun getImage(id: String): Image?
    fun deleteImage(id: String)
}