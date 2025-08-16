package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.ImageService
import com.arnas.klatrebackend.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Boulder", description = "Boulder CRUD operations")
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class BoulderController(
    private val userService: UserService,
    private val boulderService: BoulderService,
    private val imageService: ImageService
) {


    private fun validateUser(accessToken: String): User? {
        return userService.getUserByToken(accessToken)
    }

    @GetMapping("/boulders")
    open fun getBoulders(@RequestParam accessToken: String): ResponseEntity<List<Boulder>> {
        val user: User = validateUser(accessToken) ?:
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val userID: Long = userService.getUserID(accessToken)?.toLong() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val json = boulderService.getBouldersByUser(userID)
        return ResponseEntity(json, HttpStatus.OK)
    }

    @GetMapping("/boulders/place")
    open fun getBouldersByPlace(@RequestParam accessToken: String, @RequestParam placeID: Long): ResponseEntity<out Any> {
        val user: User = validateUser(accessToken) ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val userID: Long = userService.getUserID(accessToken)
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val boulders = boulderService.getBouldersByPlace(placeID)
        boulders.data?.forEach {
            it.image = imageService.getImage(it.id)?.image
        }
        return ResponseEntity(boulders.data, HttpStatus.OK)
    }

    @PostMapping("/boulders/place")
    open fun addBoulderToPlace(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<out Any> {
        val user: User = validateUser(accessToken) ?:
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val userID: Long = userService.getUserID(accessToken)?.toLong() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val placeID: Long = requestBody["placeID"]?.toLong() ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.addBoulderToPlace(userID,  placeID, requestBody)
        requestBody["image"]?.let {
            if(!serviceResult.success) return ResponseEntity(HttpStatus.BAD_REQUEST)
            imageService.storeImage(serviceResult.data!!, it)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/boulder")
    open fun putBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<Any> {
        val userID: Long = userService.getUserID(accessToken)
        boulderService.updateBoulder(userID.toLong(), requestBody)
        if(requestBody["image"] != null) {
            imageService.updateImage(requestBody["boulderID"]!!.toLong(), requestBody["image"]!!)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/boulder")
    open fun deleteBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<Any> {
        val userID: Long = userService.getUserID(accessToken)
        println("Deleting boulder with id ${requestBody["id"]}")
        boulderService.deleteBoulder(userID.toLong(), requestBody["id"]!!.toLong())

        return ResponseEntity(HttpStatus.OK)
    }


}