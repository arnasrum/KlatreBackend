package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import com.arnas.klatrebackend.interfaces.services.RouteSendServiceInterface
import com.arnas.klatrebackend.interfaces.services.UserServiceInterface
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "Boulder", description = "Boulder CRUD operations")
@RequestMapping("/boulders")
class BoulderController(
    private val userService: UserServiceInterface,
    private val boulderService: BoulderServiceInterface,
    private val imageService: ImageServiceInterface,
    private val routeSendService: RouteSendServiceInterface,
) {

    @GetMapping("/place")
    fun getBouldersByPlace(@RequestParam placeID: Long, user: User): ResponseEntity<out Any> {
        val userID: Long = user.id
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.getBouldersByPlace(placeID)
        if (!serviceResult.success) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(serviceResult.data, HttpStatus.OK)

    }

    @PostMapping("/place/add")
    fun addBoulderToPlace(
        @RequestParam placeID: Long,
        @RequestParam name: String,
        @RequestParam grade: Long,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<out Any> {
        val userID: Long = user.id
        
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(mapOf("message" to "User is not allowed to add boulder to this place"), HttpStatus.UNAUTHORIZED)
        }

        val serviceResult = boulderService.addBoulder(userID, placeID, name, grade, description)
        serviceResult.data?: return ResponseEntity.internalServerError().body(null)
        image?.let {
            if(!serviceResult.success) return ResponseEntity(HttpStatus.BAD_REQUEST)
            imageService.storeImageFile(image, serviceResult.data, userID)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/place/update")
    fun putBoulder(
        @RequestParam boulderID: Long,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) grade: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<Any> {
        val userID: Long = user.id
        
        val requestBody = mutableMapOf<String, String>().apply {
            put("boulderID", boulderID.toString())
            name?.let { put("name", it) }
            grade?.let { put("grade", it) }
            description?.let { put("description", it) }
        }
        
        boulderService.updateBoulder(boulderID, userID, requestBody, image)
        return ResponseEntity.ok("Boulder updated successfully")
    }

    @PostMapping("/place/sends")
    fun updateRouteSend(
        @RequestParam boulderID: Long,
        @RequestParam (required = false) attempts: Int?,
        @RequestParam (required = false) perceivedGrade: String?,
        @RequestParam (required = false) completed: String?,
        user: User
    ): ResponseEntity<Any> {
        val userID = user.id

        if(!routeSendService.getUserBoulderSends(userID, listOf(boulderID)).data.isNullOrEmpty()) {
            return ResponseEntity.badRequest().body("The user has already sent this route")
        }

        val additionalProps = mutableMapOf<String, String>()
        attempts?.let { additionalProps["attempts"] = it.toString() }
        perceivedGrade?.let { additionalProps["perceivedGrade"] = it }
        completed?.let { additionalProps["completed"] = it }

        routeSendService.addUserRouteSend(userID, boulderID, additionalProps)

        return ResponseEntity(HttpStatus.OK)
    }

}