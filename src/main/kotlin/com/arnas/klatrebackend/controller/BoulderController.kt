package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.ImageService
import com.arnas.klatrebackend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class BoulderController(
    private val userService: UserService,
    private val boulderRepository: BoulderRepository,
    private val boulderService: BoulderService,
    private val imageService: ImageService
) {

    @GetMapping("/boulders")
    fun getBoulders(@RequestParam accessToken: String): ResponseEntity<List<Boulder>> {
        val user: User = userService.getUserByToken(accessToken) ?:
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val json = boulderService.getBouldersByUser(user)
        //println(json)
        return ResponseEntity(json, HttpStatus.OK)
    }

    @PostMapping("/boulder")
    fun postBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<List<Boulder>> {
        val status = boulderService.addBoulder(accessToken, requestBody)
        if (requestBody["image"] != null) {
            println("Image included")
            val boulderID = status["boulderID"]?.toLong() ?: throw Exception("Boulder id not found")
            val image = requestBody["image"]
            if (image != null) {
                imageService.storeImage(boulderID, image)
            }
        } else {
            println("Image not included")
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/boulder")
    fun putBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<List<Boulder>> {
        val userID: Int = userService.getUserID(accessToken) ?: return  ResponseEntity(HttpStatus.UNAUTHORIZED)
        boulderService.updateBoulder(accessToken, requestBody)
        return ResponseEntity(HttpStatus.OK)
    }


}