package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Image

interface ImageRepositoryInterface {
    fun getImageByBoulderId(boulderId: Long): Image?
    fun deleteImage(imageId: String): Int
    fun storeImageMetaData(boulderId: Long, contentType: String, size: Long, userId: Long): String
    fun getImageMetadata(imageId: String): Image?
    fun getImageMetadataByBoulder(boulderId: Long): Image?
}