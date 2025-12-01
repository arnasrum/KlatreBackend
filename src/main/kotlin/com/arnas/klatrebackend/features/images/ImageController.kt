package com.arnas.klatrebackend.features.images

import com.arnas.klatrebackend.features.users.User
import com.arnas.klatrebackend.features.images.ImageServiceInterface
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@Tag(name = "Image", description = "Image CRUD operations")
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageServiceInterface,
) {

    @Value("\${app.image.upload.dir}")
    private lateinit var uploadDir: String

    @GetMapping("/{imageID}")
    fun getImage(@PathVariable imageID: String): ResponseEntity<Resource> {
        val image = imageService.getImageMetadata(imageID)
            ?: return ResponseEntity.notFound().build()

        val file = File("$uploadDir/${image.id}")
        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        val resource = FileSystemResource(file)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.contentType))
            .body(resource)
    }

    @PostMapping("/upload")
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("boulderID") boulderID: Long,
        user: User
    ): ResponseEntity<Map<String, Any>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "File is empty"))
        }

        file.contentType?: return ResponseEntity.badRequest().body(mapOf())

        val imageID = imageService.storeImageMetadata(
            contentType =file.contentType!!,
            size = file.size,
            userId = user.id,
        )

        val filePath = File(uploadDir, imageID)
        file.transferTo(filePath)


        return ResponseEntity.ok(mapOf(
            "imageID" to imageID,
            "url" to "/images/$imageID"
        ))
    }
}