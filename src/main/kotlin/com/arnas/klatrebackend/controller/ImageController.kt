package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.service.ImageService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.core.io.Resource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.File
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@Tag(name = "Image", description = "Image CRUD operations")
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService,
) {
    
    @Value("\${app.image.upload.dir}")
    private lateinit var uploadDir: String

    @GetMapping("/{imageID}")
    fun getImage(@PathVariable imageID: String): ResponseEntity<Resource> {
        val image = imageService.getImageMetadata(imageID).data
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
            boulderId = boulderID,
            contentType =file.contentType!!,
            size = file.size,
            userId = user.id,
        )

        if(!imageID.success) return ResponseEntity.badRequest().body(mapOf("error" to "Something went wrong when storing meta data"))
        if(imageID.data == null) return ResponseEntity.badRequest().body(mapOf("error" to "Something went wrong when storing meta data"))

        val filePath = File(uploadDir, imageID.data)
        file.transferTo(filePath)


        return ResponseEntity.ok(mapOf(
            "imageID" to imageID,
            "url" to "/images/$imageID"
        ))
    }
}