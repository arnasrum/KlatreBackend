package com.arnas.klatrebackend.features.images

import com.arnas.klatrebackend.features.images.Image

interface ImageRepositoryInterface {
    fun getImageById(id: String): Image?
    fun deleteImage(id: String): Int
    fun storeImageMetaData(contentType: String, size: Long, userId: Long): String
    fun getImageMetadata(id: String): Image?
}