package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.Image
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import com.arnas.klatrebackend.repositories.ImageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

@Service
class ImageService(
    val imageRepository: ImageRepository,
): ImageServiceInterface {

    @Value("\${app.image.upload.dir}")
    private lateinit var uploadDir: String

    fun init() {
        try {
            val path = Path(uploadDir)
            if (!path.exists()) {
                path.createDirectories()
                println("Created upload directory: $uploadDir")
            }
        } catch (e: Exception) {
            println("Failed to create upload directory: $uploadDir")
            println("Error: ${e.message}")
            throw RuntimeException("Cannot create upload directory: $uploadDir. Please check permissions or use a different directory.", e)
        }
    }

    override fun storeImageMetadata(userId: Long, contentType: String, size: Long): String {
        val id = imageRepository.storeImageMetaData(contentType, size, userId)
        return id
    }

    override fun getImageMetadata(imageId: String): Image? {
        val image = imageRepository.getImageMetadata(imageId) ?: throw RuntimeException("Image metadata not found")
        return image
    }

    override fun getImageMetadataById(id: String): Image? {
        val image = imageRepository.getImageMetadata(id) ?: throw RuntimeException("Image not found")
        return image
    }

    override fun storeImageFile(file: MultipartFile, userId: Long): String {
        init()
        if (file.isEmpty) {
            throw RuntimeException("Failed to store image. File is empty")
        }
        val imageId = storeImageMetadata(
            contentType = file.contentType!!,
            size = file.size,
            userId = userId
        )
        val filePath = File("$uploadDir/$imageId")
        file.transferTo(filePath)
        return imageId
    }

    override fun getImage(id: String): Image? {
       return imageRepository.getImageById(id)
    }

    override fun deleteImage(id: String) {
        imageRepository.deleteImage(id)
    }
}