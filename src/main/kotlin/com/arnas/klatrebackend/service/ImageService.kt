package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Image
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO

import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

@Service
class ImageService(
    val imageRepository: ImageRepository,
) {

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

    open fun storeImageMetadata(boulderID: Long,
                                contentType: String,
                                size: Long,
                                userID: Long
    ): ServiceResult<String> {
        val id = imageRepository.storeImageMetaData(boulderID, contentType, size, userID)
        return ServiceResult(data = id, message = "Image metadata stored successfully", success = true)
    }

    open fun getImageMetadata(imageID: String): ServiceResult<Image?> {
        val image = imageRepository.getImageMetaData(imageID)
        image?: return ServiceResult(data = null, message = "Image not found", success = false)
        return ServiceResult(data = image, message = "Image metadata retrieved successfully", success = true)
    }

    open fun getImageMetadataByBoulder(boulderID: Long): ServiceResult<Image?> {
        val image = imageRepository.getImageMetaDataByBoulder(boulderID) ?: return ServiceResult(data = null, message = "Image not found", success = false)
        return ServiceResult(data = image, message = "Image metadata retrieved successfully", success = true)
    }


    open fun storeImageFile(file: MultipartFile, boulderID: Long, userID: Long): ServiceResult<String> {
        try {
            init()
            if (file.isEmpty) {
                return ServiceResult(data = null, message = "File is empty", success = false)
            }

            imageRepository.getImageMetaDataByBoulder(boulderID)?.let {
                File("$uploadDir/${it.id}").delete()
                deleteImage(it)
            }

            val imageID = storeImageMetadata(
                boulderID = boulderID,
                contentType = file.contentType!!,
                size = file.size,
                userID = userID).data

            imageID?: throw Exception("Error storing image metadata")
            val filePath = File("$uploadDir/$imageID")
            file.transferTo(filePath)

            return ServiceResult(data = imageID, message = "Image stored successfully", success = true)

        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(data = null, message = "Error storing image file: ${e.message}", success = false)
        }

    }


    fun getImage(boulderID: Long): Image? {
       return imageRepository.getImageByID(boulderID)
    }

    open fun deleteImage(image: Image) {
        imageRepository.deleteImage(image.id)
    }


}