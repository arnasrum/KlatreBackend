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

    private val uploadDir: String = "./upload/"

    fun storeImage(image: MultipartFile) {


        //val userID: Int = userService.getUserID(token) ?: return // The user was not found
        println("contentType ${image.contentType}")
        //val filePath: Path = Paths.get(uploadDir)
        val fileName: String = UUID.randomUUID().toString() + ".png"
        val filePath = Path(uploadDir + fileName)
        filePath.parent.createDirectories()
        //image.transferTo(filePath)
        val imageString = Base64.getEncoder().encodeToString(image.bytes)
        imageRepository.storeImage(imageString, 1)

    }

    fun getImage(boulderID: Long): Image? {
       return imageRepository.getImageByID(boulderID)
    }


}