package com.arnas.klatrebackend.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.Path

@Service
class ImageService {

    private val uploadDir: String = "/upload"

    fun storeImage() {
        //val filePath: Path = Paths.get(uploadDir)
        val fileName = Path(uploadDir + "/" + UUID.randomUUID())
        println("Storing image to $fileName")

    }
}