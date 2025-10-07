package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.BoulderResponse
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import com.arnas.klatrebackend.interfaces.services.RouteSendServiceInterface
import com.arnas.klatrebackend.interfaces.services.UserServiceInterface
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    fun getBouldersByPlace(
        @RequestParam placeId: Long,
        @RequestParam page: Int,
        @RequestParam limit: Int,
        user: User
    ): ResponseEntity<BoulderResponse> {
        val userID: Long = user.id
        if(!userService.usersPlacePermissions(userID, placeId)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.getBouldersByPlace(placeId, page, limit)
        if (!serviceResult.success) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity.ok().body(serviceResult.data)

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

    data class BoulderUpdateRequest(
        val active: Boolean?,
        val name: String?,
        val grade: String?,
        val description: String?
        // Note: boulderID is excluded as it's in the path
    )

    @PutMapping("/update/{boulderID}")
    fun putBoulder(
        @PathVariable boulderID: Long,
        @RequestBody body: BoulderUpdateRequest,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<Map<String, Any>> {
        val userID: Long = user.id

        val requestBody = mutableMapOf<String, String>().apply {
            body.name?.let { put("name", it) }
            body.grade?.let { put("grade", it) }
            body.description?.let { put("description", it) }
            body.active?.let { put("active", it.toString()) }
        }
        boulderService.updateBoulder(boulderID, userID, requestBody, image)
        return ResponseEntity.ok(mapOf("message" to "Boulder updated successfully"))
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