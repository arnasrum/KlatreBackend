package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Image
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
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

@Service
class ImageService {


    @Autowired
    private lateinit var imageRepository: ImageRepository
    @Autowired
    private lateinit var userService: UserService

    fun storeImage(boulderID: Long, image: String) {
        imageRepository.storeImage(image, boulderID)
    }
    fun updateImage(boulderID: Long, image: String)  {
        imageRepository.deleteImage(boulderID)
        imageRepository.storeImage(image, boulderID)
    }

    fun getImage(boulderID: Long): Image? {
       return imageRepository.getImageByID(boulderID)
    }


}