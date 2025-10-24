package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Image

interface ImageRepositoryInterface {
    fun getImageById(id: String): Image?
    fun deleteImage(id: String): Int
    fun storeImageMetaData(contentType: String, size: Long, userId: Long): String
    fun getImageMetadata(id: String): Image?
}