package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.service.ImageService
import com.arnas.klatrebackend.service.UserService
import org.apache.tomcat.util.http.parser.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import javax.print.attribute.standard.Media

@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
class ImageController {

    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var imageService: ImageService
    private val uploadDir: String = "/storage"

    @PostMapping("/image")
    fun saveImage(@RequestParam("image") image: MultipartFile): ResponseEntity<String> {

        println(image)
        imageService.storeImage()
        return ResponseEntity.ok("Image uploaded successfully")
    }


}