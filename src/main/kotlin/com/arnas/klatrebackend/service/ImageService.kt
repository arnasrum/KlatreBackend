package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.util.UUID
import kotlin.io.path.Path

@Service
class ImageService {


    @Autowired
    private lateinit var imageRepository: ImageRepository

    private val uploadDir: String = "./upload/"

    fun storeImage(image: MultipartFile) {
        //val filePath: Path = Paths.get(uploadDir)
        val fileName = Path(uploadDir + UUID.randomUUID())
        fileName.toFile().createNewFile()

        val imageBytes = image.bytes


        println("contentType ${image.contentType}")
        println("Storing image to $fileName")
        println(fileName)
        image.transferTo(fileName)

    }
}