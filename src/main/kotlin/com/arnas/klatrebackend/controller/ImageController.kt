package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.service.ImageService
import com.arnas.klatrebackend.service.UserService
import org.apache.tomcat.util.http.parser.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import javax.print.attribute.standard.Media

@RestController
@CrossOrigin(origins = ["http://localhost:5173"])
@RequestMapping("/image")
class ImageController(private val userService: UserService, private val imageService: ImageService) {

    @GetMapping("")
    fun getImage(@RequestParam("boulderID") boulderID: Long, @RequestParam("accessToken") token: String): ResponseEntity<ByteArray> {
        val image: com.arnas.klatrebackend.dataclass.Image = imageService.getImage(boulderID) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(
            HttpStatus.OK,
        )
    }

    @PostMapping("")
    fun saveImage(@RequestParam("image") image: MultipartFile, @RequestParam("accessToken") token: String): ResponseEntity<String> {
        //imageService.storeImage(image)
        return ResponseEntity.ok("Image uploaded successfully")
        //return mapOf("status" to "200")
    }



}